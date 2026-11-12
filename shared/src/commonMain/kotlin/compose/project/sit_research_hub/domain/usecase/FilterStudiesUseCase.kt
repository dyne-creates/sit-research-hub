package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.Study
import compose.project.sit_research_hub.domain.model.StudyType
import compose.project.sit_research_hub.domain.model.StudentProfile

class FilterStudiesUseCase {
    operator fun invoke(
        studies: List<Study>,
        query: String,
        type: StudyType?,
        eligibleOnly: Boolean,
        sortOrder: SortOrder,
        profile: StudentProfile?
    ): List<Study> = studies.asSequence()
        .filter { query.isBlank() || it.title.contains(query.trim(), ignoreCase = true) || it.description.contains(query.trim(), ignoreCase = true) }
        .filter { type == null || it.type == type }
        .filter { !eligibleOnly || profile != null && it.isEligibleFor(profile) }
        .let { filtered ->
            when (sortOrder) {
                SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdAt }
                SortOrder.TITLE_A_TO_Z -> filtered.sortedBy { it.title.trim().lowercase() }
            }
        }
        .toList()

    private fun Study.isEligibleFor(profile: StudentProfile): Boolean {
        val programMatches = allowedPrograms.isEmpty() || allowedPrograms.any {
            normalizeProgram(it) == normalizeProgram(profile.program)
        }
        val yearMatches = allowedYearLevels.isEmpty() || allowedYearLevels.any {
            normalize(it) == normalize(profile.yearLevel)
        }
        return programMatches && yearMatches
    }

    private fun normalizeProgram(value: String): String = when (normalize(value)) {
        "BSINFORMATIONTECHNOLOGY", "BACHELOROFSCIENCEININFORMATIONTECHNOLOGY" -> "BSIT"
        "BSCOMPUTERSCIENCE", "BACHELOROFSCIENCEINCOMPUTERSCIENCE" -> "BSCS"
        "BSCOMPUTERENGINEERING", "BACHELOROFSCIENCEINCOMPUTERENGINEERING" -> "BSCPE"
        else -> normalize(value)
    }

    private fun normalize(value: String): String = value.filter(Char::isLetterOrDigit).uppercase()
}
