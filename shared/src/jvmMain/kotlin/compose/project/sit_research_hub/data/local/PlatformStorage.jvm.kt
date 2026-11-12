package compose.project.sit_research_hub.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import java.util.prefs.Preferences

actual class PreferencesStore {
    private val prefs = Preferences.userRoot().node("sit-research-hub")
    actual fun readSettings() = AppSettings(
        if (prefs.get("theme", ThemeMode.LIGHT.name) == ThemeMode.DARK.name) ThemeMode.DARK else ThemeMode.LIGHT,
        if (prefs.get("sort", SortOrder.NEWEST_FIRST.name) == SortOrder.TITLE_A_TO_Z.name) SortOrder.TITLE_A_TO_Z else SortOrder.NEWEST_FIRST
    )
    actual fun writeSettings(settings: AppSettings) {
        prefs.put("theme", settings.themeMode.name)
        prefs.put("sort", settings.sortOrder.name)
    }
}
actual fun createAppDatabase(): AppDatabase = Room.inMemoryDatabaseBuilder<AppDatabase>()
    .setDriver(BundledSQLiteDriver())
    .build()
