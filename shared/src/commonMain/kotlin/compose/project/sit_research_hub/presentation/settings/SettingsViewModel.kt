package compose.project.sit_research_hub.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.sit_research_hub.domain.model.AppSettings
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val settings: StateFlow<AppSettings> = repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())
    fun setTheme(theme: ThemeMode) = update { copy(themeMode = theme) }
    fun setSortOrder(sort: SortOrder) = update { copy(sortOrder = sort) }
    private fun update(transform: AppSettings.() -> AppSettings) { viewModelScope.launch { repository.update(settings.value.transform()) } }
}

