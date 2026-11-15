package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*

class ValidateStudyUseCase {
    operator fun invoke(study: Study, forPublish: Boolean): List<String> = buildList {
        if (study.title.trim().length < 3) add("Enter a title of at least 3 characters.")
        if (study.title.length > Limits.TITLE_MAX) add("The title is too long.")
        if (study.description.isBlank()) add("Enter a description.")
        if (forPublish) {
            if (study.questions.isEmpty()) add("Add at least one question before publishing.")
            study.questions.forEachIndexed { index, question ->
                val n = index + 1
                if (question.text.isBlank()) add("Question $n needs text.")
                if (question.type.hasOptions) {
                    val options = question.options.map(String::trim)
                    if (options.size < 2 || options.any(String::isEmpty)) add("Question $n needs at least two non-empty options.")
                    else if (options.toSet().size != options.size) add("Question $n has duplicate options.")
                }
                if (question.type == QuestionType.RATING_SCALE && question.ratingMax !in 3..10)
                    add("Question $n: the rating scale must go up to 3 to 10.")
            }
        }
    }
}
