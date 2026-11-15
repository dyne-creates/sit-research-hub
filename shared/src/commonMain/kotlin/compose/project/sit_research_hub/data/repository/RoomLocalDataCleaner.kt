package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.ParticipationDao
import compose.project.sit_research_hub.data.local.StudyDao

class RoomLocalDataCleaner(private val studies: StudyDao, private val participations: ParticipationDao) {
    suspend fun clear() {
        studies.deleteAll()
        participations.deleteAll()
    }
}
