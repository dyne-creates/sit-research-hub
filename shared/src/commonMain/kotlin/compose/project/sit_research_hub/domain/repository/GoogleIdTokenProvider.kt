package compose.project.sit_research_hub.domain.repository
import compose.project.sit_research_hub.domain.model.AppResult

sealed class GoogleCredentialResult {
    data class IdToken(val idToken: String, val nonce: String, val email: String) : GoogleCredentialResult()
    data class AuthCode(val code: String, val codeVerifier: String) : GoogleCredentialResult()
}

interface GoogleIdTokenProvider {
    suspend fun getCredential(): AppResult<GoogleCredentialResult>
}
