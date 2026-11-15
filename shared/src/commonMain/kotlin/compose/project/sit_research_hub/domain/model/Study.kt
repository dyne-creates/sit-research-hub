package compose.project.sit_research_hub.domain.model

enum class StudyType(val label: String) { QUALITATIVE("Qualitative"), QUANTITATIVE("Quantitative") }
enum class StudyStatus { DRAFT, PUBLISHED, CLOSED }

data class Eligibility(
    val programs: List<String> = emptyList(),
    val yearLevels: List<Int> = emptyList(),
    val description: String = ""
)

data class Study(
    val id: String,
    val ownerId: String,
    val ownerName: String,
    val title: String,
    val description: String,
    val type: StudyType,
    val eligibility: Eligibility = Eligibility(),
    val isAnonymous: Boolean = false,
    val oneResponsePerRespondent: Boolean = true,
    val status: StudyStatus = StudyStatus.PUBLISHED,
    val questions: List<Question> = emptyList(),
    val createdAt: String = ""
) {
    // Compatibility constructor for the original local demo model.
    constructor(
        id: String, title: String, type: StudyType, description: String, researcherName: String,
        requirements: List<String>, questions: List<Question>, createdAt: Long,
        allowedPrograms: Set<String> = emptySet(), allowedYearLevels: Set<String> = emptySet()
    ) : this(
        id = id, ownerId = "", ownerName = researcherName, title = title, description = description,
        type = type,
        eligibility = Eligibility(allowedPrograms.toList(), allowedYearLevels.mapNotNull { it.firstOrNull(Char::isDigit)?.digitToIntOrNull() }, requirements.joinToString("; ")),
        questions = questions, createdAt = createdAt.toString()
    )

    val researcherName: String get() = ownerName
    val requirements: List<String> get() = eligibility.description.split("; ").filter(String::isNotBlank)
    val allowedPrograms: Set<String> get() = eligibility.programs.toSet()
    val allowedYearLevels: Set<String> get() = eligibility.yearLevels.map { "${it}${if (it == 1) "st" else if (it == 2) "nd" else if (it == 3) "rd" else "th"} Year" }.toSet()
}

data class StudyFilter(val search: String = "", val type: StudyType? = null, val eligibleOnly: Boolean = false) {
    val isActive get() = search.isNotBlank() || type != null || eligibleOnly
}

sealed class EligibilityResult {
    data object Eligible : EligibilityResult()
    data class NotEligible(val reason: String) : EligibilityResult()
}

sealed class StudyAccess {
    data object CanAnswer : StudyAccess()
    data object OwnStudy : StudyAccess()
    data class NotEligible(val reason: String) : StudyAccess()
    data object AlreadyAnswered : StudyAccess()
    data object NotOpen : StudyAccess()
}
