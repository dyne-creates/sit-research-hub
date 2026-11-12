package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.domain.model.Submission
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
class SubmissionRepositoryImpl : SubmissionRepository {
    private val submissions = mutableListOf<Submission>()
    override fun hasSubmitted(studyId: String, respondentId: String): Boolean = submissions.any { it.studyId == studyId && it.respondentId == respondentId }
    override fun save(submission: Submission) { submissions += submission }
}

