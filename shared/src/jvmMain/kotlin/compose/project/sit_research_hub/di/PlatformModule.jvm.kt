package compose.project.sit_research_hub.di
import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.local.createAppDatabase
actual fun platformDatabase(): AppDatabase = createAppDatabase()
actual fun platformPreferences(): PreferencesStore = PreferencesStore()
