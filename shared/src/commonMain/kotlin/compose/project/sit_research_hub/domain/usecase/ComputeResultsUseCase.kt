package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*
import kotlin.math.round

fun Double.oneDecimal(): String = (round(this * 10) / 10).toString()

class ComputeResultsUseCase {
    operator fun invoke(study: Study, responses: List<StudyResponse>): List<QuestionResult> = study.questions.map { question ->
        val given = responses.mapNotNull { response ->
            response.answers.firstOrNull { it.questionId == question.id }?.values?.takeIf { it.isNotEmpty() }?.let { response to it }
        }
        when (question.type) {
            QuestionType.MULTIPLE_CHOICE, QuestionType.CHECKBOX -> {
                val counts = question.options.map { option ->
                    val count = given.count { option in it.second }
                    OptionCount(option, count, percent(count, given.size))
                }
                QuestionResult(question, given.size, counts, null, null, emptyList())
            }
            QuestionType.RATING_SCALE -> {
                val ratings = given.mapNotNull { it.second.firstOrNull()?.toIntOrNull() }.filter { it in 1..question.ratingMax }.sorted()
                val median = when {
                    ratings.isEmpty() -> null
                    ratings.size % 2 == 1 -> ratings[ratings.size / 2].toDouble()
                    else -> (ratings[ratings.size / 2 - 1] + ratings[ratings.size / 2]) / 2.0
                }
                val counts = (1..question.ratingMax).map { value -> val count = ratings.count { it == value }; OptionCount("$value", count, percent(count, ratings.size)) }
                QuestionResult(question, ratings.size, counts, ratings.takeIf { it.isNotEmpty() }?.average(), median, emptyList())
            }
            QuestionType.SHORT_ANSWER, QuestionType.PARAGRAPH -> QuestionResult(
                question, given.size, emptyList(), null, null, given.map { (response, values) -> TextAnswer(response.respondentName, values.first()) }
            )
        }
    }
    private fun percent(count: Int, total: Int) = if (total == 0) 0.0 else count * 100.0 / total
}

fun summaryText(study: Study, total: Int, results: List<QuestionResult>): String = buildString {
    appendLine(study.title); appendLine("Total responses: $total")
    results.forEachIndexed { index, result ->
        appendLine(); appendLine("${index + 1}. ${result.question.text} (${result.answered} answered)")
        result.counts.forEach { appendLine("   ${it.label}: ${it.count} (${it.percent.oneDecimal()}%)") }
        result.mean?.let { appendLine("   Mean: ${it.oneDecimal()}") }
        result.median?.let { appendLine("   Median: ${it.oneDecimal()}") }
        if (result.textAnswers.isNotEmpty()) appendLine("   ${result.textAnswers.size} written answers")
    }
}
