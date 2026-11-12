package compose.project.sit_research_hub.data.local
import compose.project.sit_research_hub.domain.model.AppSettings
expect class PreferencesStore {
    fun readSettings(): AppSettings
    fun writeSettings(settings: AppSettings)
}
