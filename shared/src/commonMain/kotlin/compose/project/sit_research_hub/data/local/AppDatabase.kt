package compose.project.sit_research_hub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StudyEntity::class, SubmissionEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao
    abstract fun submissionDao(): SubmissionDao
}

expect fun createAppDatabase(): AppDatabase

