package compose.project.sit_research_hub.domain.model

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppException) : AppResult<Nothing>()
}