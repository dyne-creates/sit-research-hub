package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {
    private val state = MutableStateFlow(store.readSettings())
    override val settings: Flow<AppSettings> = state
    override suspend fun update(settings: AppSettings) {
        store.writeSettings(settings)
        state.value = settings
    }
}

