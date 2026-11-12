package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun update(settings: AppSettings)
}

