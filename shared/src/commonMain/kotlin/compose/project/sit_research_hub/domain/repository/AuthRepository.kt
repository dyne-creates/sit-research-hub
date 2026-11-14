package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthState
import compose.project.sit_research_hub.domain.model.StudentProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun restoreSession()
    suspend fun accountExists(email: String): AppResult<Boolean>
    suspend fun signInWithGoogle(credential: GoogleCredentialResult): AppResult<Unit>
    suspend fun beginGoogleSignUp(): AppResult<String>
    suspend fun cancelGoogleSignUp(signupId: String)
    suspend fun signUpWithGoogle(credential: GoogleCredentialResult, signupId: String): AppResult<Unit>
    suspend fun setProfileConsentAccepted()
    suspend fun saveProfile(profile: StudentProfile): AppResult<Unit>
    suspend fun signOut()
}
