package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*

class CheckEligibilityUseCase {
    operator fun invoke(study: Study, profile: StudentProfile): EligibilityResult {
        val eligibility = study.eligibility
        if (eligibility.programs.isNotEmpty() && eligibility.programs.none { normalizeProgram(it) == normalizeProgram(profile.program) })
            return EligibilityResult.NotEligible("Open only to: ${eligibility.programs.joinToString()}.")
        val year = profile.yearLevelNumber
        if (eligibility.yearLevels.isNotEmpty() && (year == null || year !in eligibility.yearLevels))
            return EligibilityResult.NotEligible("Open only to year level ${eligibility.yearLevels.sorted().joinToString()}.")
        return EligibilityResult.Eligible
    }

    private fun normalizeProgram(value: String): String = when (value.filter(Char::isLetterOrDigit).uppercase()) {
        "BSINFORMATIONTECHNOLOGY", "BACHELOROFSCIENCEININFORMATIONTECHNOLOGY" -> "BSIT"
        "BSCOMPUTERSCIENCE", "BACHELOROFSCIENCEINCOMPUTERSCIENCE" -> "BSCS"
        "BSCOMPUTERENGINEERING", "BACHELOROFSCIENCEINCOMPUTERENGINEERING" -> "BSCPE"
        else -> value.filter(Char::isLetterOrDigit).uppercase()
    }

    fun access(study: Study, userId: String, profile: StudentProfile, alreadyAnswered: Boolean): StudyAccess = when {
        study.ownerId == userId -> StudyAccess.OwnStudy
        study.status != StudyStatus.PUBLISHED -> StudyAccess.NotOpen
        else -> when (val eligibility = invoke(study, profile)) {
            is EligibilityResult.NotEligible -> StudyAccess.NotEligible(eligibility.reason)
            EligibilityResult.Eligible -> if (study.oneResponsePerRespondent && alreadyAnswered) StudyAccess.AlreadyAnswered else StudyAccess.CanAnswer
        }
    }
}
