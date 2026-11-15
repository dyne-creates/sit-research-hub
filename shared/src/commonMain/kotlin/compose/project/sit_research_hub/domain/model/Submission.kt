package compose.project.sit_research_hub.domain.model

data class Participation(val studyId: String, val studyTitle: String, val researcherName: String, val createdAt: String)
data class StudyResponse(val id: String, val respondentName: String?, val answers: List<Answer>, val createdAt: String)
data class InterviewSlot(
    val id: String, val studyId: String, val startsAt: String, val location: String, val notes: String,
    val bookedBy: String?, val bookedByName: String?
) { val isAvailable get() = bookedBy == null }
