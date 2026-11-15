package compose.project.sit_research_hub.domain.model

data class StudentProfile(
    val program: String,
    val yearLevel: String
) {
    val yearLevelNumber: Int? get() = yearLevel.firstOrNull(Char::isDigit)?.digitToIntOrNull()
}
