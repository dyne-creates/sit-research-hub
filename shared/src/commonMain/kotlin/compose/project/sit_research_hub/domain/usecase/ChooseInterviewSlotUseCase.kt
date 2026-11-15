package compose.project.sit_research_hub.domain.usecase

import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.InterviewRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class CreateInterviewSlotUseCase(private val interviews: InterviewRepository) {
    suspend operator fun invoke(study: Study, date: String, time: String, location: String, notes: String): AppResult<InterviewSlot> {
        if (study.type != StudyType.QUALITATIVE) return AppResult.Failure(ValidationException("Interviews are only for qualitative studies."))
        val startsAt = try { LocalDateTime(LocalDate.parse(date.trim()), LocalTime.parse(time.trim())).toString() }
        catch (_: IllegalArgumentException) { return AppResult.Failure(ValidationException("Use YYYY-MM-DD and 24-hour HH:MM time.")) }
        if (location.isBlank()) return AppResult.Failure(ValidationException("Enter the interview location or meeting link."))
        return interviews.create(study.id, startsAt, location.trim(), notes.trim())
    }
}

class ChooseInterviewSlotUseCase(private val interviews: InterviewRepository) {
    suspend operator fun invoke(study: Study, userId: String, slot: InterviewSlot, all: List<InterviewSlot>): AppResult<Unit> = when {
        study.ownerId == userId -> AppResult.Failure(OwnStudyException())
        !slot.isAvailable -> AppResult.Failure(ConflictException("That interview slot was just taken."))
        all.any { it.bookedBy == userId } -> AppResult.Failure(ConflictException("You already chose an interview slot."))
        else -> interviews.book(slot.id)
    }
}
