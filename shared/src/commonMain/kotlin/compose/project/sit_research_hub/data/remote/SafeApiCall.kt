package compose.project.sit_research_hub.data.remote

import compose.project.sit_research_hub.domain.model.AppException
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.NetworkException
import kotlinx.coroutines.*

suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: AppException) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        AppResult.Failure(NetworkException("Cannot reach the server. Check your connection and try again.", e))
    }
