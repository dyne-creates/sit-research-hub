package compose.project.sit_research_hub.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthState
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.repository.AuthRepository
import compose.project.sit_research_hub.domain.repository.GoogleIdTokenProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val authState: StateFlow<AuthState> = repository.authState
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private var authJob: Job? = null

    init { viewModelScope.launch { repository.restoreSession() } }

    fun signIn(provider: GoogleIdTokenProvider) {
        if (_uiState.value == AuthUiState.Loading) return
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            // Existing installations may not yet have the optional permit-cleanup
            // RPC deployed. Do not prevent established users from reaching Google
            // sign-in when that maintenance call is unavailable.
            repository.prepareGoogleSignIn()
            when (val credential = provider.getCredential()) {
                is AppResult.Failure -> finishProviderFailure(credential.error.message)
                is AppResult.Success -> finish(repository.signInWithGoogle(credential.data))
            }
        }
    }

    fun signUp(provider: GoogleIdTokenProvider) {
        if (_uiState.value == AuthUiState.Loading) return
        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val intent = repository.beginGoogleSignUp()) {
                is AppResult.Failure -> finish(intent)
                is AppResult.Success -> when (val credential = provider.getCredential()) {
                    is AppResult.Failure -> {
                        repository.cancelGoogleSignUp(intent.data)
                        finishSignUpFailure(credential.error.message)
                    }
                    is AppResult.Success -> {
                        val result = repository.signUpWithGoogle(credential.data, intent.data)
                        if (result is AppResult.Failure) repository.cancelGoogleSignUp(intent.data)
                        finishSignUpResult(result)
                    }
                }
            }
        }
    }

    fun cancelSignIn() { authJob?.cancel(); authJob = null; _uiState.value = AuthUiState.Idle }

    fun saveProfile(profile: StudentProfile, consentAccepted: Boolean) {
        if (_uiState.value == AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            if (consentAccepted) repository.setProfileConsentAccepted()
            finish(repository.saveProfile(profile))
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() { _uiState.value = AuthUiState.Idle }
    private fun finish(result: AppResult<Unit>) {
        _uiState.value = when (result) {
            is AppResult.Success -> AuthUiState.Idle
            is AppResult.Failure -> AuthUiState.Error(result.error.message ?: "Something went wrong. Please try again.")
        }
    }

    private fun finishProviderFailure(message: String?) {
        val detail = message.orEmpty()
        _uiState.value = if (detail.contains("account not found", ignoreCase = true) ||
            detail.contains("database error saving new user", ignoreCase = true) ||
            detail.contains("sign up first", ignoreCase = true)
        ) {
            AuthUiState.Error("Account not found. Sign up first.")
        } else {
            AuthUiState.Error(detail.ifBlank { "Something went wrong. Please try again." })
        }
    }

    private fun finishSignUpResult(result: AppResult<Unit>) {
        when (result) {
            is AppResult.Success -> _uiState.value = AuthUiState.Idle
            is AppResult.Failure -> finishSignUpFailure(result.error.message)
        }
    }

    private fun finishSignUpFailure(message: String?) {
        val detail = message.orEmpty()
        val normalized = when {
            detail.contains("account was already created", ignoreCase = true) ||
                detail.contains("you already have an account", ignoreCase = true) ||
                detail.contains("already registered", ignoreCase = true) ||
                detail.contains("user already exists", ignoreCase = true) ||
                detail.contains("duplicate key", ignoreCase = true) ->
                "You already have an account. Sign in instead."
            detail.contains("account not found", ignoreCase = true) ||
                detail.contains("sign up first", ignoreCase = true) ->
                "Account not found. Sign up first."
            detail.contains("database error saving new user", ignoreCase = true) ->
                "Sign-up could not be completed because the account service rejected the request. If you already have an account, sign in. Otherwise, please try again later."
            else -> detail.ifBlank { "Sign-up failed. Please try again." }
        }
        _uiState.value = AuthUiState.Error(normalized)
    }
}
