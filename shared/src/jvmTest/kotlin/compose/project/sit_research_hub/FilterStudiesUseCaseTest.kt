package compose.project.sit_research_hub

import compose.project.sit_research_hub.domain.model.Question
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.Study
import compose.project.sit_research_hub.domain.model.StudyType
import compose.project.sit_research_hub.domain.usecase.FilterStudiesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterStudiesUseCaseTest {
    private val filterStudies = FilterStudiesUseCase()
    private val studies = listOf(
        study(id = "new", title = "Zebra Study", createdAt = 20),
        study(id = "old", title = "Alpha Study", createdAt = 10),
        study(
            id = "restricted",
            title = "Programming Survey",
            createdAt = 15,
            type = StudyType.QUANTITATIVE,
            allowedPrograms = setOf("BS Information Technology"),
            allowedYearLevels = setOf("2nd Year", "3rd Year")
        )
    )

    @Test
    fun sortOrderChangesStudyOrdering() {
        assertEquals(
            listOf("new", "restricted", "old"),
            filterStudies(studies, "", null, false, SortOrder.NEWEST_FIRST, null).map { it.id }
        )
        assertEquals(
            listOf("old", "restricted", "new"),
            filterStudies(studies, "", null, false, SortOrder.TITLE_A_TO_Z, null).map { it.id }
        )
    }

    @Test
    fun searchAndTypeFiltersCanBeCombined() {
        val result = filterStudies(
            studies,
            query = "programming",
            type = StudyType.QUANTITATIVE,
            eligibleOnly = false,
            sortOrder = SortOrder.NEWEST_FIRST,
            profile = null
        )

        assertEquals(listOf("restricted"), result.map { it.id })
    }

    @Test
    fun eligibilityMatchesProgramAliasesAndYearLevel() {
        val eligible = filterStudies(
            studies,
            query = "",
            type = null,
            eligibleOnly = true,
            sortOrder = SortOrder.NEWEST_FIRST,
            profile = StudentProfile(program = "BSIT", yearLevel = "2nd Year")
        )
        val ineligible = filterStudies(
            studies,
            query = "",
            type = null,
            eligibleOnly = true,
            sortOrder = SortOrder.NEWEST_FIRST,
            profile = StudentProfile(program = "BSCS", yearLevel = "2nd Year")
        )

        assertEquals(listOf("new", "restricted", "old"), eligible.map { it.id })
        assertEquals(listOf("new", "old"), ineligible.map { it.id })
    }

    private fun study(
        id: String,
        title: String,
        createdAt: Long,
        type: StudyType = StudyType.QUALITATIVE,
        allowedPrograms: Set<String> = emptySet(),
        allowedYearLevels: Set<String> = emptySet()
    ) = Study(
        id = id,
        title = title,
        type = type,
        description = "$title description",
        researcherName = "Research Team",
        requirements = emptyList(),
        questions = emptyList<Question>(),
        createdAt = createdAt,
        allowedPrograms = allowedPrograms,
        allowedYearLevels = allowedYearLevels
    )
}
