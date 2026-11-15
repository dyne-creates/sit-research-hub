package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.*
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun observeStudies(): Flow<List<Study>>
    suspend fun refresh(): AppResult<Unit>
    suspend fun getStudy(id: String): AppResult<Study>
    suspend fun saveStudy(study: Study): AppResult<Study>
    suspend fun setStatus(id: String, status: StudyStatus): AppResult<Unit>
}
