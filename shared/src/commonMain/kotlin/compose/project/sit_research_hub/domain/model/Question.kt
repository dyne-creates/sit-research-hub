package compose.project.sit_research_hub.domain.model

object Limits {
    const val TITLE_MAX = 150
    const val SHORT_MAX = 200
    const val PARAGRAPH_MAX = 2000
}

enum class QuestionType(val label: String) {
    MULTIPLE_CHOICE("Multiple choice"), CHECKBOX("Checkbox"), SHORT_ANSWER("Short answer"),
    PARAGRAPH("Paragraph"), RATING_SCALE("Rating scale");
    val hasOptions get() = this == MULTIPLE_CHOICE || this == CHECKBOX
}

data class Question(
    val id: String,
    val text: String,
    val type: QuestionType,
    val isRequired: Boolean = true,
    val options: List<String> = emptyList(),
    val ratingMax: Int = 5
)

data class Answer(val questionId: String, val values: List<String>)
data class OptionCount(val label: String, val count: Int, val percent: Double)
data class TextAnswer(val respondent: String?, val text: String)
data class QuestionResult(
    val question: Question, val answered: Int, val counts: List<OptionCount>,
    val mean: Double?, val median: Double?, val textAnswers: List<TextAnswer>
)
