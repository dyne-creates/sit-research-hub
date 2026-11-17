package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.mapper.toDomain
import compose.project.sit_research_hub.data.remote.InterviewApi
import compose.project.sit_research_hub.data.remote.NewSlotDto
import compose.project.sit_research_hub.data.remote.safeApiCall
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.InterviewRepository

class InterviewRepositoryImpl(private val api: InterviewApi) : InterviewRepository {
    override suspend fun slots(studyId: String): AppResult<List<InterviewSlot>> = when (val result = safeApiCall { api.slots(studyId) }) {
        is AppResult.Failure -> result
        is AppResult.Success -> AppResult.Success(result.data.map { it.toDomain() })
    }
    override suspend fun bookedSlots(userId: String): AppResult<List<InterviewSlot>> = when (val result = safeApiCall { api.bookedSlots(userId) }) {
        is AppResult.Failure -> result
        is AppResult.Success -> AppResult.Success(result.data.map { it.toDomain() })
    }
    override suspend fun create(studyId: String, startsAt: String, location: String, notes: String): AppResult<InterviewSlot> =
        when (val result = safeApiCall { api.create(NewSlotDto(studyId, startsAt, location, notes)) }) {
            is AppResult.Failure -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    override suspend fun delete(slotId: String): AppResult<Unit> = safeApiCall { api.delete(slotId) }
    override suspend fun book(slotId: String): AppResult<Unit> = safeApiCall { api.book(slotId) }
}
