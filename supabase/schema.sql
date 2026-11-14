create schema if not exists private;
revoke all on schema private from public, anon, authenticated;

drop table if exists private.google_signup_intents;
create table private.google_signup_intents (
    signup_id uuid primary key default gen_random_uuid(),
    created_at timestamptz not null default now(),
    expires_at timestamptz not null
);
revoke all on table private.google_signup_intents from public, anon, authenticated;

drop table if exists private.recent_google_signups;
create table private.recent_google_signups (
    user_id uuid primary key,
    signup_id uuid not null,
    created_at timestamptz not null default now()
);
revoke all on table private.recent_google_signups from public, anon, authenticated;

create table if not exists private.app_accounts (
    user_id uuid primary key references auth.users(id) on delete cascade,
    email text not null unique,
    created_at timestamptz not null default now()
);
revoke all on table private.app_accounts from public, anon, authenticated;

create or replace function public.enforce_email_domain()
returns trigger language plpgsql security definer set search_path = '' as $$
declare consumed_signup_id uuid;
begin
  if new.email is null or lower(new.email) not like '%@s.ubaguio.edu' then
    raise exception 'Only @s.ubaguio.edu accounts are allowed';
  end if;

  with candidate as (
    select signup_id
    from private.google_signup_intents
    where expires_at > now()
    order by created_at
    limit 1
    for update skip locked
  )
  delete from private.google_signup_intents as intents
  using candidate
  where intents.signup_id = candidate.signup_id
  returning intents.signup_id into consumed_signup_id;
  if consumed_signup_id is null then
    raise exception 'Account not found. Sign up first.';
  end if;
  insert into private.recent_google_signups(user_id, signup_id)
  values (new.id, consumed_signup_id)
  on conflict (user_id) do update set signup_id = excluded.signup_id;
  return new;
end;
$$;

drop trigger if exists enforce_email_domain on auth.users;
create trigger enforce_email_domain
    before insert on auth.users
    for each row execute function public.enforce_email_domain();

create or replace function public.register_app_account()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
  insert into private.app_accounts(user_id, email)
  values (new.id, lower(trim(new.email)))
  on conflict (user_id) do update set email = excluded.email;
  return new;
end;
$$;

drop trigger if exists register_app_account on auth.users;
create trigger register_app_account
    after insert on auth.users
    for each row execute function public.register_app_account();

create table if not exists public.profiles (
    id          uuid primary key references auth.users(id) on delete cascade,
    email       text not null,
    full_name   text,
    program     text not null,
    year_level  int  not null check (year_level between 1 and 6),
    created_at  timestamptz not null default now()
    );

-- Existing users with completed profiles are already established app accounts.
insert into private.app_accounts(user_id, email)
select users.id, lower(trim(users.email))
from auth.users as users
join public.profiles as profiles on profiles.id = users.id
where users.email is not null
on conflict (user_id) do nothing;

alter table public.profiles enable row level security;

drop policy if exists "profiles: read own" on public.profiles;
create policy "profiles: read own" on public.profiles
  for select to authenticated using (auth.uid() = id);

drop policy if exists "profiles: insert own" on public.profiles;
create policy "profiles: insert own" on public.profiles
  for insert to authenticated
  with check (
    auth.uid() = id
    and lower(email) = lower(auth.jwt() ->> 'email')
    and lower(email) like '%@s.ubaguio.edu'
  );

drop policy if exists "profiles: update own" on public.profiles;
create policy "profiles: update own" on public.profiles
  for update to authenticated
    using (auth.uid() = id) with check (auth.uid() = id);

-- Each sign-up receives a short-lived, single-use permit. The auth trigger
-- requires it before creating a Supabase user, while account_exists only
-- recognizes accounts registered by the app or with a completed profile.
drop function if exists public.begin_google_signup();
create function public.begin_google_signup()
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare new_signup_id uuid;
begin
  delete from private.google_signup_intents where expires_at <= now();
  delete from private.recent_google_signups where created_at <= now() - interval '3 minutes';
  insert into private.google_signup_intents(signup_id, expires_at)
  values (gen_random_uuid(), now() + interval '3 minutes')
  returning signup_id into new_signup_id;
  return new_signup_id;
end;
$$;

revoke all on function public.begin_google_signup() from public;
grant execute on function public.begin_google_signup() to anon, authenticated;

drop function if exists public.complete_google_signup();
create or replace function public.complete_google_signup(p_signup_id uuid)
returns boolean
language plpgsql
security definer
set search_path = ''
as $$
declare
  current_user_id uuid := auth.uid();
  current_email text := lower(trim(auth.jwt() ->> 'email'));
  consumed_signup_id uuid;
begin
  if current_user_id is null or current_email is null or current_email not like '%@s.ubaguio.edu' then
    raise exception 'Sign up with your @s.ubaguio.edu Google account.';
  end if;

  delete from private.recent_google_signups
  where user_id = current_user_id and created_at > now() - interval '3 minutes'
  returning signup_id into consumed_signup_id;
  delete from private.recent_google_signups
  where user_id = current_user_id and created_at <= now() - interval '3 minutes';
  if consumed_signup_id is not null then
    delete from private.google_signup_intents
    where signup_id in (p_signup_id, consumed_signup_id);
    return true;
  end if;

  if exists (select 1 from private.app_accounts where user_id = current_user_id) then
    delete from private.google_signup_intents where signup_id = p_signup_id;
    return false;
  end if;

  delete from private.google_signup_intents where signup_id = p_signup_id and expires_at > now();
  if not found then
    raise exception 'Sign up first to create your Research Hub account.';
  end if;
  insert into private.app_accounts(user_id, email) values (current_user_id, current_email)
  on conflict (user_id) do nothing;
  return true;
end;
$$;

revoke all on function public.complete_google_signup(uuid) from public;
grant execute on function public.complete_google_signup(uuid) to authenticated;

create or replace function public.cancel_google_signup(p_signup_id uuid)
returns void
language sql
security definer
set search_path = ''
as $$
  delete from private.google_signup_intents where signup_id = p_signup_id;
$$;

revoke all on function public.cancel_google_signup(uuid) from public;
grant execute on function public.cancel_google_signup(uuid) to anon, authenticated;

create or replace function public.account_exists(p_email text)
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1
    from private.app_accounts as accounts
    where accounts.email = lower(trim(p_email))
      and accounts.email like '%@s.ubaguio.edu'
  );
$$;

revoke all on function public.account_exists(text) from public;
grant execute on function public.account_exists(text) to anon, authenticated;
