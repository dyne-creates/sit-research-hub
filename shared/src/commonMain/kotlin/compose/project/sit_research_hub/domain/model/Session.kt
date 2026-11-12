package compose.project.sit_research_hub.domain.model

data class UserProfile(
    val id: String = "placeholder-student",
    val program: String,
    val yearLevel: String,
    val privacyConsent: Boolean
)

