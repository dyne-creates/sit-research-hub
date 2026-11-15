-- Week 3 research data schema. Apply after the existing sign-in/profile schema.
create table if not exists public.studies (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) on delete cascade,
  owner_name text not null,
  title text not null check (char_length(title) between 3 and 150),
  description text not null,
  study_type text not null check (study_type in ('QUALITATIVE','QUANTITATIVE')),
  requirements text not null default '',
  eligible_programs text[] not null default '{}',
  eligible_year_levels int[] not null default '{}',
  is_anonymous boolean not null default false,
  one_response_per_respondent boolean not null default true,
  status text not null default 'DRAFT' check (status in ('DRAFT','PUBLISHED','CLOSED')),
  questions jsonb not null default '[]'::jsonb,
  created_at timestamptz not null default now()
);
alter table public.studies enable row level security;
drop policy if exists "studies: read published or own" on public.studies;
create policy "studies: read published or own" on public.studies for select to authenticated using (status = 'PUBLISHED' or owner_id = auth.uid());
drop policy if exists "studies: insert own" on public.studies;
create policy "studies: insert own" on public.studies for insert to authenticated with check (owner_id = auth.uid());
drop policy if exists "studies: update own" on public.studies;
create policy "studies: update own" on public.studies for update to authenticated using (owner_id = auth.uid()) with check (owner_id = auth.uid());
drop policy if exists "studies: delete own" on public.studies;
create policy "studies: delete own" on public.studies for delete to authenticated using (owner_id = auth.uid());
grant select, insert, update, delete on public.studies to authenticated;

create table if not exists public.participations (
  id uuid primary key default gen_random_uuid(),
  study_id uuid not null references public.studies(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  study_title text not null,
  researcher_name text not null,
  created_at timestamptz not null default now()
);
create index if not exists participations_user_idx on public.participations(user_id);
create index if not exists participations_study_user_idx on public.participations(study_id, user_id);
alter table public.participations enable row level security;
drop policy if exists "participations: read own" on public.participations;
create policy "participations: read own" on public.participations for select to authenticated using (user_id = auth.uid());
grant select on public.participations to authenticated;

create table if not exists public.responses (
  id uuid primary key default gen_random_uuid(),
  study_id uuid not null references public.studies(id) on delete cascade,
  respondent_id uuid references auth.users(id) on delete set null,
  respondent_name text,
  answers jsonb not null,
  created_at timestamptz not null default now()
);
create index if not exists responses_study_idx on public.responses(study_id);
alter table public.responses enable row level security;
drop policy if exists "responses: owner reads" on public.responses;
create policy "responses: owner reads" on public.responses for select to authenticated
  using (exists (select 1 from public.studies s where s.id = study_id and s.owner_id = auth.uid()));
grant select on public.responses to authenticated;
revoke insert, update, delete on public.responses from anon, authenticated;

create table if not exists public.interview_slots (
  id uuid primary key default gen_random_uuid(),
  study_id uuid not null references public.studies(id) on delete cascade,
  starts_at timestamp not null,
  location text not null,
  notes text not null default '',
  booked_by uuid references auth.users(id) on delete set null,
  booked_by_name text,
  created_at timestamptz not null default now()
);
create index if not exists slots_study_idx on public.interview_slots(study_id);
alter table public.interview_slots enable row level security;
drop policy if exists "slots: read" on public.interview_slots;
create policy "slots: read" on public.interview_slots for select to authenticated using (
  exists (select 1 from public.studies s where s.id = study_id and s.owner_id = auth.uid())
  or booked_by = auth.uid()
  or (booked_by is null and exists (select 1 from public.studies s where s.id = study_id and s.status = 'PUBLISHED'))
);
drop policy if exists "slots: owner inserts" on public.interview_slots;
create policy "slots: owner inserts" on public.interview_slots for insert to authenticated
  with check (exists (select 1 from public.studies s where s.id = study_id and s.owner_id = auth.uid()));
drop policy if exists "slots: owner deletes" on public.interview_slots;
create policy "slots: owner deletes" on public.interview_slots for delete to authenticated
  using (exists (select 1 from public.studies s where s.id = study_id and s.owner_id = auth.uid()));
grant select, insert, delete on public.interview_slots to authenticated;

create or replace function public.submit_response(p_study_id uuid, p_answers jsonb)
returns void language plpgsql security definer set search_path = public as $$
declare s public.studies%rowtype; p public.profiles%rowtype; uid uuid := auth.uid();
begin
  if uid is null then raise exception 'NOT_SIGNED_IN'; end if;
  select * into s from public.studies where id = p_study_id;
  if not found or s.status <> 'PUBLISHED' then raise exception 'STUDY_NOT_AVAILABLE'; end if;
  if s.owner_id = uid then raise exception 'OWN_STUDY'; end if;
  select * into p from public.profiles where id = uid;
  if not found then raise exception 'PROFILE_REQUIRED'; end if;
  if cardinality(s.eligible_programs) > 0 and not (p.program = any(s.eligible_programs)) then raise exception 'NOT_ELIGIBLE'; end if;
  if cardinality(s.eligible_year_levels) > 0 and not (p.year_level = any(s.eligible_year_levels)) then raise exception 'NOT_ELIGIBLE'; end if;
  if jsonb_typeof(p_answers) <> 'array' then raise exception 'INVALID_ANSWERS'; end if;
  perform pg_advisory_xact_lock(hashtext(p_study_id::text || uid::text));
  if s.one_response_per_respondent and exists (select 1 from public.participations where study_id = p_study_id and user_id = uid) then
    raise exception 'DUPLICATE_SUBMISSION'; end if;
  insert into public.responses(study_id, respondent_id, respondent_name, answers)
  values (p_study_id, case when s.is_anonymous then null else uid end,
          case when s.is_anonymous then null else coalesce(p.full_name, p.email) end, p_answers);
  insert into public.participations(study_id, user_id, study_title, researcher_name)
  values (p_study_id, uid, s.title, s.owner_name);
end; $$;
revoke all on function public.submit_response(uuid, jsonb) from public, anon;
grant execute on function public.submit_response(uuid, jsonb) to authenticated;

create or replace function public.book_interview_slot(p_slot_id uuid)
returns void language plpgsql security definer set search_path = public as $$
declare uid uuid := auth.uid(); sl public.interview_slots%rowtype; s public.studies%rowtype; nm text;
begin
  if uid is null then raise exception 'NOT_SIGNED_IN'; end if;
  select * into sl from public.interview_slots where id = p_slot_id for update;
  if not found then raise exception 'SLOT_NOT_FOUND'; end if;
  select * into s from public.studies where id = sl.study_id;
  if s.status <> 'PUBLISHED' then raise exception 'STUDY_NOT_AVAILABLE'; end if;
  if s.owner_id = uid then raise exception 'OWN_STUDY'; end if;
  if sl.booked_by is not null then raise exception 'SLOT_TAKEN'; end if;
  if exists (select 1 from public.interview_slots where study_id = sl.study_id and booked_by = uid) then raise exception 'ALREADY_BOOKED'; end if;
  select coalesce(full_name, email) into nm from public.profiles where id = uid;
  update public.interview_slots set booked_by = uid, booked_by_name = nm where id = p_slot_id;
end; $$;
revoke all on function public.book_interview_slot(uuid) from public, anon;
grant execute on function public.book_interview_slot(uuid) to authenticated;
