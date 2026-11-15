package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SubmissionRepository {
    fun observeParticipations(): Flow<List<Participation>>
    suspend fun refreshParticipations(): AppResult<Unit>
    suspend fun hasParticipated(studyId: String): Boolean
    suspend fun submit(study: Study, answers: List<Answer>): AppResult<Unit>
    suspend fun fetchResponses(studyId: String): AppResult<List<StudyResponse>>
}
