package compose.project.sit_research_hub.data.remote

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.mapper.toStoredSession
import compose.project.sit_research_hub.domain.model.*
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionManager(private val store: PreferencesStore, private val authApi: AuthApi) {
    private val mutex = Mutex()
    suspend fun accessToken(): String = store.readSession()?.accessToken
        ?: throw AuthException("Your session ended. Please sign in again.")

    suspend fun refreshAfter(failedToken: String): String = mutex.withLock {
        val stored = store.readSession() ?: throw AuthException("Your session ended. Please sign in again.")
        if (stored.accessToken != failedToken) return@withLock stored.accessToken
        when (val refreshed = authApi.refresh(stored.refreshToken)) {
            is AppResult.Success -> refreshed.data.toStoredSession().also { store.saveSession(it) }.accessToken
            is AppResult.Failure -> throw refreshed.error
        }
    }
}

class RestClient(private val client: HttpClient, private val session: SessionManager) {
    suspend fun send(method: HttpMethod, path: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        var token = session.accessToken()
        var response = execute(method, path, token, block)
        if (response.status == HttpStatusCode.Unauthorized) {
            token = session.refreshAfter(token)
            response = execute(method, path, token, block)
        }
        if (!response.status.isSuccess()) throw response.toAppException()
        return response
    }

    private suspend fun execute(method: HttpMethod, path: String, token: String, block: HttpRequestBuilder.() -> Unit) =
        client.request { this.method = method; url(path); bearerAuth(token); block() }
}

private suspend fun HttpResponse.toAppException(): AppException {
    val text = runCatching { bodyAsText() }.getOrDefault("")
    return when {
        "DUPLICATE_SUBMISSION" in text -> DuplicateSubmissionException()
        "OWN_STUDY" in text -> OwnStudyException()
        "NOT_ELIGIBLE" in text -> NotEligibleException()
        "PROFILE_REQUIRED" in text -> ValidationException("Finish your profile setup first.")
        "STUDY_NOT_AVAILABLE" in text -> ConflictException("This study is no longer available.")
        "SLOT_TAKEN" in text -> ConflictException("That interview slot was just taken.")
        "ALREADY_BOOKED" in text -> ConflictException("You already chose an interview slot.")
        "SLOT_NOT_FOUND" in text -> ConflictException("That interview slot no longer exists.")
        status.value == 401 || status.value == 403 -> AuthException("You are not allowed to do that.")
        status.value >= 500 -> NetworkException("The server is having problems. Try again later.")
        else -> NetworkException("The request failed (${status.value}).")
    }
}
