package compose.project.sit_research_hub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studies")
data class StudyEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val ownerName: String,
    val title: String,
    val description: String,
    val studyType: String,
    val requirements: String,
    val programsJson: String,
    val yearLevelsJson: String,
    val isAnonymous: Boolean,
    val oneResponse: Boolean,
    val status: String,
    val questionsJson: String,
    val createdAt: String
)

@Entity(tableName = "participations")
data class ParticipationEntity(
    @PrimaryKey val id: String,
    val studyId: String,
    val studyTitle: String,
    val researcherName: String,
    val createdAt: String
)
