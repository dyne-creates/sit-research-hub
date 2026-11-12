package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.Study
import kotlinx.coroutines.flow.Flow
interface StudyRepository {
    fun observeStudies(): Flow<List<Study>>
    fun findById(id: String): Study?
}

