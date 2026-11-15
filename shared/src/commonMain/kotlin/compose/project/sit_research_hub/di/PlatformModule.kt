package compose.project.sit_research_hub.di

import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.domain.repository.TextSharer
expect fun platformDatabase(): AppDatabase
expect fun platformPreferences(): PreferencesStore
expect fun platformTextSharer(): TextSharer

