package compose.project.sit_research_hub.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.domain.model.StudentProfile
import platform.Foundation.NSUserDefaults

actual class PreferencesStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    actual fun readSettings() = AppSettings(
        if (defaults.stringForKey("theme") == ThemeMode.DARK.name) ThemeMode.DARK else ThemeMode.LIGHT,
        if (defaults.stringForKey("sort") == SortOrder.TITLE_A_TO_Z.name) SortOrder.TITLE_A_TO_Z else SortOrder.NEWEST_FIRST
    )
    actual fun writeSettings(settings: AppSettings) {
        defaults.setObject(settings.themeMode.name, forKey = "theme")
        defaults.setObject(settings.sortOrder.name, forKey = "sort")
    }
    actual suspend fun saveSession(session: StoredSession) {
        defaults.setObject(session.accessToken, forKey = "access_token")
        defaults.setObject(session.refreshToken, forKey = "refresh_token")
        defaults.setObject(session.userId, forKey = "user_id")
        defaults.setObject(session.email, forKey = "user_email")
        session.displayName?.let { defaults.setObject(it, forKey = "user_display_name") }
            ?: defaults.removeObjectForKey("user_display_name")
    }
    actual suspend fun readSession(): StoredSession? {
        val accessToken = defaults.stringForKey("access_token") ?: return null
        val refreshToken = defaults.stringForKey("refresh_token") ?: return null
        val userId = defaults.stringForKey("user_id") ?: return null
        val email = defaults.stringForKey("user_email") ?: return null
        return StoredSession(accessToken, refreshToken, userId, email, defaults.stringForKey("user_display_name"))
    }
    actual suspend fun saveProfile(profile: StudentProfile) {
        defaults.setObject(profile.program, forKey = "profile_program")
        defaults.setObject(profile.yearLevel, forKey = "profile_year_level")
    }
    actual suspend fun readProfile(): StudentProfile? {
        val program = defaults.stringForKey("profile_program") ?: return null
        val yearLevel = defaults.stringForKey("profile_year_level") ?: return null
        return StudentProfile(program, yearLevel)
    }
    actual suspend fun saveConsentAccepted() { defaults.setBool(true, forKey = "profile_consent_accepted") }
    actual suspend fun hasConsentAccepted(): Boolean = defaults.boolForKey("profile_consent_accepted")
    actual suspend fun clearSession() {
        listOf("access_token", "refresh_token", "user_id", "user_email", "user_display_name", "profile_program", "profile_year_level", "profile_consent_accepted")
            .forEach { defaults.removeObjectForKey(it) }
    }
}
actual fun createAppDatabase(): AppDatabase = Room.inMemoryDatabaseBuilder<AppDatabase>()
    .setDriver(BundledSQLiteDriver())
    .build()
