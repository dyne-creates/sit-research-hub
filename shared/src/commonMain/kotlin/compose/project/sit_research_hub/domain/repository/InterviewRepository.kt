package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.*

interface InterviewRepository {
    suspend fun slots(studyId: String): AppResult<List<InterviewSlot>>
    suspend fun create(studyId: String, startsAt: String, location: String, notes: String): AppResult<InterviewSlot>
    suspend fun delete(slotId: String): AppResult<Unit>
    suspend fun book(slotId: String): AppResult<Unit>
}
