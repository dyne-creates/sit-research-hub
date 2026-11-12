package compose.project.sit_research_hub.di

import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
expect fun platformDatabase(): AppDatabase
expect fun platformPreferences(): PreferencesStore

