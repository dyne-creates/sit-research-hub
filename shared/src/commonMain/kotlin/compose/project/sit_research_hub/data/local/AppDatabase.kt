package compose.project.sit_research_hub.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

const val DATABASE_FILE = "researchhub.db"

@Database(entities = [StudyEntity::class, ParticipationEntity::class], version = 2, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao
    abstract fun participationDao(): ParticipationDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun createAppDatabase(): AppDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS submissions")
        connection.execSQL("DROP TABLE IF EXISTS studies")
        connection.execSQL("CREATE TABLE IF NOT EXISTS studies (id TEXT NOT NULL PRIMARY KEY, ownerId TEXT NOT NULL, ownerName TEXT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, studyType TEXT NOT NULL, requirements TEXT NOT NULL, programsJson TEXT NOT NULL, yearLevelsJson TEXT NOT NULL, isAnonymous INTEGER NOT NULL, oneResponse INTEGER NOT NULL, status TEXT NOT NULL, questionsJson TEXT NOT NULL, createdAt TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS participations (id TEXT NOT NULL PRIMARY KEY, studyId TEXT NOT NULL, studyTitle TEXT NOT NULL, researcherName TEXT NOT NULL, createdAt TEXT NOT NULL)")
    }
}

fun buildAppDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase = builder
    .addMigrations(MIGRATION_1_2)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
