package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.SubmissionRepository

class ValidateAnswersUseCase {
    operator fun invoke(study: Study, answers: Map<String, List<String>>): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        for (question in study.questions) {
            val values = answers[question.id].orEmpty().map(String::trim).filter(String::isNotEmpty)
            if (values.isEmpty()) {
                if (question.isRequired) errors[question.id] = "This question is required."
                continue
            }
            when (question.type) {
                QuestionType.MULTIPLE_CHOICE -> if (values.size != 1 || values[0] !in question.options) errors[question.id] = "Choose one of the options."
                QuestionType.CHECKBOX -> if (values.any { it !in question.options }) errors[question.id] = "Choose from the listed options."
                QuestionType.SHORT_ANSWER -> if (values[0].length > Limits.SHORT_MAX) errors[question.id] = "Keep it under ${Limits.SHORT_MAX} characters."
                QuestionType.PARAGRAPH -> if (values[0].length > Limits.PARAGRAPH_MAX) errors[question.id] = "Keep it under ${Limits.PARAGRAPH_MAX} characters."
                QuestionType.RATING_SCALE -> {
                    val rating = values.singleOrNull()?.toIntOrNull()
                    if (rating == null || rating !in 1..question.ratingMax) errors[question.id] = "Pick a rating from 1 to ${question.ratingMax}."
                }
            }
        }
        return errors
    }
}

class SubmitResponseUseCase(
    private val validate: ValidateAnswersUseCase,
    private val check: CheckEligibilityUseCase,
    private val submissions: SubmissionRepository
) {
    suspend operator fun invoke(study: Study, userId: String, profile: StudentProfile, answers: Map<String, List<String>>): AppResult<Unit> {
        when (check.access(study, userId, profile, submissions.hasParticipated(study.id))) {
            StudyAccess.CanAnswer -> Unit
            StudyAccess.OwnStudy -> return AppResult.Failure(OwnStudyException())
            is StudyAccess.NotEligible -> return AppResult.Failure(NotEligibleException())
            StudyAccess.AlreadyAnswered -> return AppResult.Failure(DuplicateSubmissionException())
            StudyAccess.NotOpen -> return AppResult.Failure(ConflictException("This study is not accepting responses."))
        }
        if (validate(study, answers).isNotEmpty()) return AppResult.Failure(ValidationException("Please answer the highlighted questions."))
        val answerList = study.questions.mapNotNull { question ->
            answers[question.id].orEmpty().map(String::trim).filter(String::isNotEmpty).takeIf { it.isNotEmpty() }?.let { Answer(question.id, it) }
        }
        return submissions.submit(study, answerList)
    }
}
