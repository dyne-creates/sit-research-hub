package compose.project.sit_research_hub.domain.model

enum class QuestionType { SHORT_ANSWER, LONG_ANSWER, SINGLE_CHOICE, MULTIPLE_CHOICE, RATING }
data class Question(
    val id: String,
    val text: String,
    val type: QuestionType,
    val isRequired: Boolean = true,
    val options: List<String> = emptyList()
)

