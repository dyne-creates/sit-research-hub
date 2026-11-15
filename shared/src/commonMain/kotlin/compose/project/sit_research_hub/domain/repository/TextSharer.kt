package compose.project.sit_research_hub.domain.repository

interface TextSharer {
    val actionLabel: String
    fun share(title: String, text: String): String
}
