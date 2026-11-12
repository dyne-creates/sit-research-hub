package compose.project.sit_research_hub.domain.model

data class Answer(val questionId: String, val value: String)
data class Submission(val studyId: String, val respondentId: String, val answers: List<Answer>)

