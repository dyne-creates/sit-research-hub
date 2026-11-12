package compose.project.sit_research_hub.presentation.navigation

object Destination {
    const val PERSONALIZATION = "personalization"
    const val HOME = "home"
    const val RESEARCH_DETAILS = "research_details/{researchId}"
    const val SETTINGS = "settings"

    fun researchDetails(researchId: String): String {
        return "research_details/$researchId"
    }
}
