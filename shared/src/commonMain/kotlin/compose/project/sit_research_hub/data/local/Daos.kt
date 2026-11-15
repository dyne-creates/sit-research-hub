package compose.project.sit_research_hub.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao interface StudyDao {
    @Query("SELECT * FROM studies ORDER BY createdAt DESC") fun observeAll(): Flow<List<StudyEntity>>
    @Query("SELECT * FROM studies WHERE id = :id") suspend fun getById(id: String): StudyEntity?
    @Upsert suspend fun upsert(item: StudyEntity)
    @Upsert suspend fun upsertAll(items: List<StudyEntity>)
    @Query("DELETE FROM studies WHERE id NOT IN (:ids)") suspend fun deleteNotIn(ids: List<String>)
    @Query("DELETE FROM studies") suspend fun deleteAll()
}

@Dao interface ParticipationDao {
    @Query("SELECT * FROM participations ORDER BY createdAt DESC") fun observeAll(): Flow<List<ParticipationEntity>>
    @Query("SELECT COUNT(*) FROM participations WHERE studyId = :studyId") suspend fun countForStudy(studyId: String): Int
    @Upsert suspend fun upsert(item: ParticipationEntity)
    @Upsert suspend fun upsertAll(items: List<ParticipationEntity>)
    @Query("DELETE FROM participations WHERE id NOT IN (:ids)") suspend fun deleteNotIn(ids: List<String>)
    @Query("DELETE FROM participations") suspend fun deleteAll()
}
