package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.local.StoredSession
import compose.project.sit_research_hub.data.mapper.toDomain
import compose.project.sit_research_hub.data.mapper.toStoredSession
import compose.project.sit_research_hub.data.mapper.toUser
import compose.project.sit_research_hub.data.remote.AuthApi
import compose.project.sit_research_hub.data.remote.ProfileDto
import compose.project.sit_research_hub.di.AppConfig
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthException
import compose.project.sit_research_hub.domain.model.AuthState
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.repository.AuthRepository
import compose.project.sit_research_hub.domain.repository.GoogleCredentialResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val store: PreferencesStore,
    private val config: AppConfig
) : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun restoreSession() {
        val stored = store.readSession()
        if (stored == null || !config.isAllowedEmail(stored.email)) {
            store.clearSession()
            _authState.value = AuthState.SignedOut
            return
        }
        val session = when (val refreshed = api.refresh(stored.refreshToken)) {
            is AppResult.Success -> refreshed.data.toStoredSession().also { store.saveSession(it) }
            is AppResult.Failure -> {
                if (refreshed.error is AuthException) {
                    store.clearSession()
                    _authState.value = AuthState.SignedOut
                    return
                }
                stored
            }
        }
        _authState.value = resolveState(session)
    }

    override suspend fun accountExists(email: String): AppResult<Boolean> {
        val normalized = email.trim().lowercase()
        if (!config.isAllowedEmail(normalized)) {
            return AppResult.Failure(AuthException("Use your @${config.allowedEmailDomain} university email."))
        }
        return api.accountExists(normalized)
    }

    override suspend fun signInWithGoogle(credential: GoogleCredentialResult): AppResult<Unit> {
        if (credential is GoogleCredentialResult.IdToken) {
            val exists = when (val result = accountExists(credential.email)) {
                is AppResult.Failure -> return result
                is AppResult.Success -> result.data
            }
            if (!exists) return AppResult.Failure(AuthException("Account not found. Sign up first."))
        }
        return authenticate(credential, allowProfileSetup = false)
    }

    override suspend fun beginGoogleSignUp(): AppResult<String> = api.beginGoogleSignUp()

    override suspend fun cancelGoogleSignUp(signupId: String) {
        api.cancelGoogleSignUp(signupId)
    }

    override suspend fun signUpWithGoogle(credential: GoogleCredentialResult, signupId: String): AppResult<Unit> {
        if (credential is GoogleCredentialResult.IdToken) {
            when (val exists = accountExists(credential.email)) {
                is AppResult.Failure -> return exists
                is AppResult.Success -> if (exists.data) {
                    cancelGoogleSignUp(signupId)
                    return AppResult.Failure(AuthException("Account was already created. Sign in instead."))
                }
            }
        }
        return authenticate(credential, allowProfileSetup = true, signupId = signupId)
    }

    private suspend fun authenticate(
        credential: GoogleCredentialResult,
        allowProfileSetup: Boolean,
        signupId: String? = null
    ): AppResult<Unit> {
        val response = when (credential) {
            is GoogleCredentialResult.IdToken -> api.signInWithIdToken(credential.idToken, credential.nonce)
            is GoogleCredentialResult.AuthCode -> api.exchangeAuthCode(credential.code, credential.codeVerifier)
        }
        return when (response) {
            is AppResult.Failure -> response
            is AppResult.Success -> {
                if (!config.isAllowedEmail(response.data.user.email)) {
                    AppResult.Failure(AuthException("Only @${config.allowedEmailDomain} accounts can use SIT Research Hub."))
                } else {
                    val session = response.data.toStoredSession()
                    if (allowProfileSetup) {
                        when (val registered = api.completeGoogleSignUp(session.accessToken, requireNotNull(signupId))) {
                            is AppResult.Failure -> return registered
                            is AppResult.Success -> if (!registered.data) {
                                return AppResult.Failure(AuthException("Account was already created. Sign in instead."))
                            }
                        }
                    } else {
                        when (val exists = accountExists(session.email)) {
                            is AppResult.Failure -> return exists
                            is AppResult.Success -> if (!exists.data) {
                                return AppResult.Failure(AuthException("Account not found. Sign up first."))
                            }
                        }
                    }
                    store.clearSession()
                    store.saveSession(session)
                    val resolvedState = resolveState(session)
                    _authState.value = resolvedState
                    AppResult.Success(Unit)
                }
            }
        }
    }

    override suspend fun saveProfile(profile: StudentProfile): AppResult<Unit> {
        val user = when (val state = _authState.value) {
            is AuthState.NeedsProfile -> state.user
            is AuthState.SignedIn -> state.user
            else -> return AppResult.Failure(AuthException("Please sign in first."))
        }
        val session = store.readSession() ?: return AppResult.Failure(AuthException("Session expired. Please sign in again."))
        val yearNumber = profile.yearLevel.firstOrNull(Char::isDigit)?.digitToIntOrNull()
            ?: return AppResult.Failure(compose.project.sit_research_hub.domain.model.ValidationException("Choose your year level."))
        val dto = ProfileDto(user.id, user.email, user.displayName, profile.program.trim(), yearNumber)
        return when (val result = api.upsertProfile(session.accessToken, dto)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                store.saveProfile(profile)
                _authState.value = AuthState.SignedIn(user, profile)
                AppResult.Success(Unit)
            }
        }
    }

    override suspend fun setProfileConsentAccepted() {
        store.saveConsentAccepted()
    }

    override suspend fun signOut() {
        store.clearSession()
        _authState.value = AuthState.SignedOut
    }

    private suspend fun resolveState(session: StoredSession): AuthState {
        val user = session.toUser()
        var profile = store.readProfile()
        if (profile == null) {
            val remote = api.fetchProfile(session.accessToken, session.userId)
            if (remote is AppResult.Success) {
                profile = remote.data?.toDomain()
                profile?.let { store.saveProfile(it) }
            }
        }
        return if (profile != null) AuthState.SignedIn(user, profile) else AuthState.NeedsProfile(user)
    }
}
