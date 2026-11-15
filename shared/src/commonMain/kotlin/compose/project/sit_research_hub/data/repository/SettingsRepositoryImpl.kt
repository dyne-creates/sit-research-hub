package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {
    private val current = MutableStateFlow(store.readSettings())
    override val settings: StateFlow<AppSettings> = current
    override suspend fun update(settings: AppSettings) {
        store.writeSettings(settings)
        current.value = settings
    }
}
