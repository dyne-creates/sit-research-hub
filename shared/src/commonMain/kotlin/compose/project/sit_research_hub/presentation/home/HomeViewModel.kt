package compose.project.sit_research_hub.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.Study
import compose.project.sit_research_hub.domain.model.StudyType
import compose.project.sit_research_hub.domain.repository.SettingsRepository
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.usecase.FilterStudiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeFilters(
    val query: String = "",
    val type: StudyType? = null,
    val eligibleOnly: Boolean = false
)

class HomeViewModel(
    studies: StudyRepository,
    settings: SettingsRepository,
    private val filterStudies: FilterStudiesUseCase
) : ViewModel() {
    private val filters = MutableStateFlow(HomeFilters())
    private val profile = MutableStateFlow<StudentProfile?>(null)

    val visibleStudies: StateFlow<List<Study>> = combine(studies.observeStudies(), settings.settings, filters, profile) { all, appSettings, currentFilters, currentProfile ->
        filterStudies(all, currentFilters.query, currentFilters.type, currentFilters.eligibleOnly, appSettings.sortOrder, currentProfile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(query: String) { filters.value = filters.value.copy(query = query) }
    fun updateType(type: StudyType?) { filters.value = filters.value.copy(type = type) }
    fun updateEligibleOnly(enabled: Boolean) { filters.value = filters.value.copy(eligibleOnly = enabled) }
    fun updateProfile(profile: StudentProfile) { this.profile.value = profile }
}
