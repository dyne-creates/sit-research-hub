package compose.project.sit_research_hub.data.repository

import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.domain.model.Question
import compose.project.sit_research_hub.domain.model.QuestionType
import compose.project.sit_research_hub.domain.model.Study
import compose.project.sit_research_hub.domain.model.StudyType
import compose.project.sit_research_hub.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
class StudyRepositoryImpl(private val database: AppDatabase) : StudyRepository {
    private val studies = listOf(
        Study(
            id = "1", title = "Study Habits of SIT Students", type = StudyType.QUALITATIVE,
            description = "This study explores how SIT students plan and complete their academic work.",
            researcherName = "SIT Research Team",
            requirements = listOf("SIT student", "Any year level", "Willing to share study habits"),
            questions = listOf(
                Question("1a", "What usually helps you study effectively?", QuestionType.LONG_ANSWER),
                Question("1b", "How often do you study outside class?", QuestionType.SINGLE_CHOICE, options = listOf("Every day", "Several times a week", "Rarely"))
            ), createdAt = 2L
        ),
        Study(
            id = "2", title = "Preferred Development Tools", type = StudyType.QUANTITATIVE,
            description = "A short survey about the tools SIT students use for programming activities.",
            researcherName = "SIT Research Team",
            requirements = listOf("BS Information Technology student", "2nd Year or higher"),
            questions = listOf(
                Question("2a", "Which tool do you use most often?", QuestionType.SINGLE_CHOICE, options = listOf("Android Studio", "IntelliJ IDEA", "Visual Studio Code")),
                Question("2b", "Rate your confidence with development tools.", QuestionType.RATING)
            ), createdAt = 1L,
            allowedPrograms = setOf("BS Information Technology"),
            allowedYearLevels = setOf("2nd Year", "3rd Year", "4th Year")
        )
    )
    private val state = MutableStateFlow(studies)
    init { seedIfNeeded() }
    private fun seedIfNeeded() {
        // The Room-backed repository is initialized by AppContainer. The seed is kept
        // here so Week 2 remains deterministic without any network dependency.
        state.value = studies
    }
    override fun observeStudies(): Flow<List<Study>> = state
    override fun findById(id: String): Study? = studies.firstOrNull { it.id == id }
}

