package compose.project.sit_research_hub.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.domain.model.StudentProfile
import java.util.prefs.Preferences

actual class PreferencesStore {
    private val prefs = Preferences.userRoot().node("sit-research-hub")
    private val sessionPrefs = prefs.node("session")
    actual fun readSettings() = AppSettings(
        if (prefs.get("theme", ThemeMode.LIGHT.name) == ThemeMode.DARK.name) ThemeMode.DARK else ThemeMode.LIGHT,
        if (prefs.get("sort", SortOrder.NEWEST_FIRST.name) == SortOrder.TITLE_A_TO_Z.name) SortOrder.TITLE_A_TO_Z else SortOrder.NEWEST_FIRST
    )
    actual fun writeSettings(settings: AppSettings) {
        prefs.put("theme", settings.themeMode.name)
        prefs.put("sort", settings.sortOrder.name)
    }
    actual suspend fun saveSession(session: StoredSession) {
        sessionPrefs.put("access_token", session.accessToken)
        sessionPrefs.put("refresh_token", session.refreshToken)
        sessionPrefs.put("user_id", session.userId)
        sessionPrefs.put("user_email", session.email)
        session.displayName?.let { sessionPrefs.put("user_display_name", it) }
            ?: sessionPrefs.remove("user_display_name")
    }
    actual suspend fun readSession(): StoredSession? {
        val accessToken = sessionPrefs.get("access_token", null) ?: return null
        val refreshToken = sessionPrefs.get("refresh_token", null) ?: return null
        val userId = sessionPrefs.get("user_id", null) ?: return null
        val email = sessionPrefs.get("user_email", null) ?: return null
        return StoredSession(accessToken, refreshToken, userId, email, sessionPrefs.get("user_display_name", null))
    }
    actual suspend fun saveProfile(profile: StudentProfile) {
        sessionPrefs.put("profile_program", profile.program)
        sessionPrefs.put("profile_year_level", profile.yearLevel)
    }
    actual suspend fun readProfile(): StudentProfile? {
        val program = sessionPrefs.get("profile_program", null) ?: return null
        val yearLevel = sessionPrefs.get("profile_year_level", null) ?: return null
        return StudentProfile(program, yearLevel)
    }
    actual suspend fun saveConsentAccepted() { sessionPrefs.putBoolean("profile_consent_accepted", true) }
    actual suspend fun hasConsentAccepted(): Boolean = sessionPrefs.getBoolean("profile_consent_accepted", false)
    actual suspend fun clearSession() {
        listOf("access_token", "refresh_token", "user_id", "user_email", "user_display_name", "profile_program", "profile_year_level", "profile_consent_accepted")
            .forEach(sessionPrefs::remove)
    }
}
actual fun createAppDatabase(): AppDatabase = Room.inMemoryDatabaseBuilder<AppDatabase>()
    .setDriver(BundledSQLiteDriver())
    .build()
