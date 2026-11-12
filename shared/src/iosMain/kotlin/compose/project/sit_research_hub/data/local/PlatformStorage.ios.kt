package compose.project.sit_research_hub.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
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
}
actual fun createAppDatabase(): AppDatabase = Room.inMemoryDatabaseBuilder<AppDatabase>()
    .setDriver(BundledSQLiteDriver())
    .build()
