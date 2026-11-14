package compose.project.sit_research_hub.domain.model

data class User(val id: String, val email: String, val displayName: String?)

sealed class AuthState {
    data object Checking : AuthState()
    data object SignedOut : AuthState()
    data class NeedsProfile(val user: User) : AuthState()
    data class SignedIn(val user: User, val profile: StudentProfile) : AuthState()
}
