package compose.project.sit_research_hub.presentation.navigation

object Destination {
    const val PERSONALIZATION = "personalization"
    const val HOME = "home"
    const val RESEARCH_DETAILS = "research_details/{researchId}"
    const val SETTINGS = "settings"
    const val YOUR_RESEARCHES = "your_researches"
    const val ADD_RESEARCH = "add_research"
    const val RESEARCH_SUMMARY = "research_summary"
    const val MY_RESPONSES = "my_responses"
    const val ACCOUNT = "account"
    const val INTERVIEW_SCHEDULES = "interview_schedules"

    fun researchDetails(researchId: String): String {
        return "research_details/$researchId"
    }
}
