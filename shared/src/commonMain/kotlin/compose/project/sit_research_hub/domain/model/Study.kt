package compose.project.sit_research_hub.domain.model

enum class StudyType { QUALITATIVE, QUANTITATIVE }
data class Study(
    val id: String,
    val title: String,
    val type: StudyType,
    val description: String,
    val researcherName: String,
    val requirements: List<String>,
    val questions: List<Question>,
    val createdAt: Long,
    val allowedPrograms: Set<String> = emptySet(),
    val allowedYearLevels: Set<String> = emptySet(),
    val oneResponsePerRespondent: Boolean = true,
    val ownerId: String = "researcher-1"
)

