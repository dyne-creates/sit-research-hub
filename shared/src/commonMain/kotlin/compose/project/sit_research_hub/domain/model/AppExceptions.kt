package compose.project.sit_research_hub.domain.model

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
class AuthException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ValidationException(message: String) : AppException(message)