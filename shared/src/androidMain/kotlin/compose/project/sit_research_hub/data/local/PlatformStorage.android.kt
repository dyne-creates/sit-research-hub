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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AndroidPlatformContext { lateinit var application: Application }
private val Application.settingsDataStore by preferencesDataStore("research_hub_settings")

actual class PreferencesStore {
    private val themeKey = stringPreferencesKey("theme")
    private val sortKey = stringPreferencesKey("sort")
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
}

actual fun createAppDatabase(): AppDatabase = Room.databaseBuilder<AppDatabase>(
    AndroidPlatformContext.application,
    "research-hub.db"
).setDriver(BundledSQLiteDriver()).build()

