package compose.project.sit_research_hub.domain.repository

import compose.project.sit_research_hub.domain.model.Submission
interface SubmissionRepository {
    fun hasSubmitted(studyId: String, respondentId: String): Boolean
    fun save(submission: Submission)
}

