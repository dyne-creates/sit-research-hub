package compose.project.sit_research_hub.data.local

import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.StudentProfile

data class StoredSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val displayName: String?
)

expect class PreferencesStore {
    fun readSettings(): AppSettings
    fun writeSettings(settings: AppSettings)
    suspend fun saveSession(session: StoredSession)
    suspend fun readSession(): StoredSession?
    suspend fun saveProfile(profile: StudentProfile)
    suspend fun readProfile(): StudentProfile?
    suspend fun saveConsentAccepted()
    suspend fun hasConsentAccepted(): Boolean
    suspend fun clearSession()
}
