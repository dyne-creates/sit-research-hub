package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.ParticipationDao
import compose.project.sit_research_hub.data.local.ParticipationEntity
import compose.project.sit_research_hub.data.mapper.*
import compose.project.sit_research_hub.data.remote.SubmissionApi
import compose.project.sit_research_hub.data.remote.safeApiCall
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubmissionRepositoryImpl(private val api: SubmissionApi, private val dao: ParticipationDao) : SubmissionRepository {
    override fun observeParticipations(): Flow<List<Participation>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun refreshParticipations(): AppResult<Unit> = when (val result = safeApiCall { api.participations() }) {
        is AppResult.Failure -> result
        is AppResult.Success -> {
            val rows = result.data.map { it.toEntity() }
            if (rows.isNotEmpty()) dao.upsertAll(rows)
            if (rows.isEmpty()) dao.deleteAll() else dao.deleteNotIn(rows.map { it.id })
            AppResult.Success(Unit)
        }
    }

    override suspend fun hasParticipated(studyId: String): Boolean = dao.countForStudy(studyId) > 0

    override suspend fun submit(study: Study, answers: List<Answer>): AppResult<Unit> = when (val result = safeApiCall { api.submit(study.id, answers.map { it.toDto() }) }) {
        is AppResult.Failure -> {
            if (result.error is DuplicateSubmissionException) saveLocalParticipation(study)
            result
        }
        is AppResult.Success -> {
            saveLocalParticipation(study)
            refreshParticipations()
            AppResult.Success(Unit)
        }
    }

    override suspend fun fetchResponses(studyId: String): AppResult<List<StudyResponse>> = when (val result = safeApiCall { api.responses(studyId) }) {
        is AppResult.Failure -> result
        is AppResult.Success -> AppResult.Success(result.data.map { it.toDomain() })
    }

    private suspend fun saveLocalParticipation(study: Study) = dao.upsert(
        ParticipationEntity("local-${study.id}", study.id, study.title, study.ownerName, "")
    )
}
