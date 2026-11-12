package compose.project.sit_research_hub.domain.model

enum class ThemeMode { LIGHT, DARK }
enum class SortOrder { NEWEST_FIRST, TITLE_A_TO_Z }
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
)

