package compose.project.sit_research_hub.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM studies ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<StudyEntity>>
    @Query("SELECT * FROM studies WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): StudyEntity?
    @Upsert
    suspend fun upsertAll(studies: List<StudyEntity>)
}

@Dao
interface SubmissionDao {
    @Query("SELECT EXISTS(SELECT 1 FROM submissions WHERE studyId = :studyId AND respondentId = :respondentId)")
    suspend fun hasSubmitted(studyId: String, respondentId: String): Boolean
    @Upsert
    suspend fun save(submission: SubmissionEntity)
}

