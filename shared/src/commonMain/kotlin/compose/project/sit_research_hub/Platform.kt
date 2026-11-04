package compose.project.sit_research_hub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform