package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.StudyDao
import compose.project.sit_research_hub.data.mapper.*
import compose.project.sit_research_hub.data.remote.StudyApi
import compose.project.sit_research_hub.data.remote.safeApiCall
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyRepositoryImpl(private val api: StudyApi, private val dao: StudyDao) : StudyRepository {
    override fun observeStudies(): Flow<List<Study>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun refresh(): AppResult<Unit> = when (val result = safeApiCall { api.list() }) {
        is AppResult.Failure -> result
        is AppResult.Success -> {
            val rows = result.data.map { it.toEntity() }
            if (rows.isNotEmpty()) dao.upsertAll(rows)
            if (rows.isEmpty()) dao.deleteAll() else dao.deleteNotIn(rows.map { it.id })
            AppResult.Success(Unit)
        }
    }

    override suspend fun getStudy(id: String): AppResult<Study> {
        dao.getById(id)?.let { return AppResult.Success(it.toDomain()) }
        return when (val result = safeApiCall { api.get(id) }) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                val dto = result.data ?: return AppResult.Failure(ConflictException("This study is no longer available."))
                dao.upsert(dto.toEntity()); AppResult.Success(dto.toDomain())
            }
        }
    }

    override suspend fun saveStudy(study: Study): AppResult<Study> = when (val result = safeApiCall { api.create(study.toNewDto()) }) {
        is AppResult.Failure -> result
        is AppResult.Success -> { dao.upsert(result.data.toEntity()); AppResult.Success(result.data.toDomain()) }
    }

    override suspend fun setStatus(id: String, status: StudyStatus): AppResult<Unit> = when (val result = safeApiCall { api.setStatus(id, status.name) }) {
        is AppResult.Failure -> result
        is AppResult.Success -> {
            val dto = result.data ?: return AppResult.Failure(AuthException("You can only change your own research."))
            dao.upsert(dto.toEntity()); AppResult.Success(Unit)
        }
    }
}
