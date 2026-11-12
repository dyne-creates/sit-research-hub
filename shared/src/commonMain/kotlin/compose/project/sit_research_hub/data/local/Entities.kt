package compose.project.sit_research_hub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studies")
data class StudyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val description: String,
    val researcherName: String,
    val requirementsJson: String,
    val questionsJson: String,
    val createdAt: Long,
    val allowedProgramsJson: String,
    val allowedYearLevelsJson: String,
    val oneResponsePerRespondent: Boolean,
    val ownerId: String
)

@Entity(tableName = "submissions", primaryKeys = ["studyId", "respondentId"])
data class SubmissionEntity(
    val studyId: String,
    val respondentId: String,
    val answersJson: String
)

