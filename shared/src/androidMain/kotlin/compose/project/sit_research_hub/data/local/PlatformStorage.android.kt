package compose.project.sit_research_hub.data.local

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.domain.model.StudentProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AndroidPlatformContext { lateinit var application: Application }
private val Application.settingsDataStore by preferencesDataStore("research_hub_settings")

actual class PreferencesStore {
    private val themeKey = stringPreferencesKey("theme")
    private val sortKey = stringPreferencesKey("sort")
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userIdKey = stringPreferencesKey("user_id")
    private val emailKey = stringPreferencesKey("user_email")
    private val displayNameKey = stringPreferencesKey("user_display_name")
    private val programKey = stringPreferencesKey("profile_program")
    private val yearLevelKey = stringPreferencesKey("profile_year_level")
    private val consentKey = booleanPreferencesKey("profile_consent_accepted")

    actual fun readSettings(): AppSettings = runBlocking {
        val values = AndroidPlatformContext.application.settingsDataStore.data.first()
        AppSettings(
            themeMode = if (values[themeKey] == ThemeMode.DARK.name) ThemeMode.DARK else ThemeMode.LIGHT,
            sortOrder = if (values[sortKey] == SortOrder.TITLE_A_TO_Z.name) SortOrder.TITLE_A_TO_Z else SortOrder.NEWEST_FIRST
        )
    }
    actual fun writeSettings(settings: AppSettings) {
        runBlocking {
            AndroidPlatformContext.application.settingsDataStore.edit { values ->
                values[themeKey] = settings.themeMode.name
                values[sortKey] = settings.sortOrder.name
            }
        }
    }

    actual suspend fun saveSession(session: StoredSession) {
        AndroidPlatformContext.application.settingsDataStore.edit { values ->
            values[accessTokenKey] = session.accessToken
            values[refreshTokenKey] = session.refreshToken
            values[userIdKey] = session.userId
            values[emailKey] = session.email
            session.displayName?.let { values[displayNameKey] = it } ?: values.remove(displayNameKey)
        }
    }

    actual suspend fun readSession(): StoredSession? {
        val values = AndroidPlatformContext.application.settingsDataStore.data.first()
        return StoredSession(
            accessToken = values[accessTokenKey] ?: return null,
            refreshToken = values[refreshTokenKey] ?: return null,
            userId = values[userIdKey] ?: return null,
            email = values[emailKey] ?: return null,
            displayName = values[displayNameKey]
        )
    }

    actual suspend fun saveProfile(profile: StudentProfile) {
        AndroidPlatformContext.application.settingsDataStore.edit { values ->
            values[programKey] = profile.program
            values[yearLevelKey] = profile.yearLevel
        }
    }

    actual suspend fun readProfile(): StudentProfile? {
        val values = AndroidPlatformContext.application.settingsDataStore.data.first()
        return StudentProfile(
            program = values[programKey] ?: return null,
            yearLevel = values[yearLevelKey] ?: return null
        )
    }

    actual suspend fun saveConsentAccepted() {
        AndroidPlatformContext.application.settingsDataStore.edit { it[consentKey] = true }
    }

    actual suspend fun hasConsentAccepted(): Boolean =
        AndroidPlatformContext.application.settingsDataStore.data.first()[consentKey] == true

    actual suspend fun clearSession() {
        AndroidPlatformContext.application.settingsDataStore.edit { values ->
            values.remove(accessTokenKey)
            values.remove(refreshTokenKey)
            values.remove(userIdKey)
            values.remove(emailKey)
            values.remove(displayNameKey)
            values.remove(programKey)
            values.remove(yearLevelKey)
            values.remove(consentKey)
        }
    }
}

actual fun createAppDatabase(): AppDatabase = Room.databaseBuilder<AppDatabase>(
    AndroidPlatformContext.application,
    "research-hub.db"
).setDriver(BundledSQLiteDriver()).build()

