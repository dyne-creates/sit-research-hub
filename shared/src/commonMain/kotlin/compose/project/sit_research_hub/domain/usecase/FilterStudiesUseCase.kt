package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*

class FilterStudiesUseCase(private val check: CheckEligibilityUseCase = CheckEligibilityUseCase()) {
    operator fun invoke(studies: List<Study>, query: String, type: StudyType?, eligibleOnly: Boolean, sortOrder: SortOrder, profile: StudentProfile?): List<Study> {
        val q = query.trim()
        val filtered = studies.filter { study ->
            (q.isEmpty() || listOf(study.title, study.description, study.ownerName).any { it.contains(q, ignoreCase = true) }) &&
                (type == null || study.type == type) &&
                (!eligibleOnly || profile != null && check(study, profile) == EligibilityResult.Eligible)
        }
        return when (sortOrder) {
            SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdAt }
            SortOrder.TITLE_A_TO_Z -> filtered.sortedBy { it.title.lowercase() }
        }
    }
}
