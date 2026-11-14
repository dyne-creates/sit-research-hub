package compose.project.sit_research_hub.data.remote

import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthException
import compose.project.sit_research_hub.domain.model.NetworkException
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(private val client: HttpClient) {

    suspend fun accountExists(email: String): AppResult<Boolean> = safeApiCall {
        val response = client.post("rest/v1/rpc/account_exists") {
            contentType(ContentType.Application.Json)
            setBody(AccountExistsRequest(email.trim().lowercase()))
        }
        if (!response.status.isSuccess()) {
            throw NetworkException("Could not verify the account right now. Please try again.")
        }
        response.body<Boolean>()
    }

    suspend fun beginGoogleSignUp(): AppResult<String> = safeApiCall {
        val response = client.post("rest/v1/rpc/begin_google_signup") {
        }
        if (!response.status.isSuccess()) {
            throw NetworkException("Could not start account setup. Please try again.")
        }
        response.body<String>()
    }

    suspend fun completeGoogleSignUp(accessToken: String, signupId: String): AppResult<Boolean> = safeApiCall {
        val response = client.post("rest/v1/rpc/complete_google_signup") {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(SignupIntentRequest(signupId))
        }
        if (!response.status.isSuccess()) {
            throw AuthException("Could not complete account setup. Please try signing up again.")
        }
        response.body<Boolean>()
    }

    suspend fun cancelGoogleSignUp(signupId: String): AppResult<Unit> = safeApiCall {
        val response = client.post("rest/v1/rpc/cancel_google_signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupIntentRequest(signupId))
        }
        if (!response.status.isSuccess()) throw NetworkException("Could not cancel account setup.")
    }

    // Android: Google ID token
    suspend fun signInWithIdToken(idToken: String, nonce: String?): AppResult<AuthSessionDto> = safeApiCall {
        val response = client.post("auth/v1/token") {
            parameter("grant_type", "id_token")
            contentType(ContentType.Application.Json)
            setBody(IdTokenRequest(idToken = idToken, nonce = nonce))
        }
        if (!response.status.isSuccess()) {
            throw AuthException("Sign-in was rejected. Use your university Google account. (${response.status.value})")
        }
        response.body<AuthSessionDto>()
    }

    // Desktop:
    suspend fun exchangeAuthCode(code: String, codeVerifier: String): AppResult<AuthSessionDto> = safeApiCall {
        val response = client.post("auth/v1/token") {
            parameter("grant_type", "pkce")
            contentType(ContentType.Application.Json)
            setBody(PkceTokenRequest(authCode = code, codeVerifier = codeVerifier))
        }
        if (!response.status.isSuccess()) {
            throw AuthException("Sign-in was rejected. Use your university Google account. (${response.status.value})")
        }
        response.body<AuthSessionDto>()
    }

    suspend fun refresh(refreshToken: String): AppResult<AuthSessionDto> = safeApiCall {
        val response = client.post("auth/v1/token") {
            parameter("grant_type", "refresh_token")
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken))
        }
        when {
            response.status.isSuccess() -> response.body<AuthSessionDto>()
            response.status.value in 400..499 -> throw AuthException("Your session expired. Please sign in again.")
            else -> throw NetworkException("The server is unavailable. Try again later.")
        }
    }

    suspend fun fetchProfile(accessToken: String, userId: String): AppResult<ProfileDto?> = safeApiCall {
        val response = client.get("rest/v1/profiles") {
            parameter("id", "eq.$userId")
            parameter("select", "*")
            bearerAuth(accessToken)
        }
        if (!response.status.isSuccess()) {
            throw NetworkException("Could not load your profile (${response.status.value}).")
        }
        response.body<List<ProfileDto>>().firstOrNull()
    }

    suspend fun upsertProfile(accessToken: String, profile: ProfileDto): AppResult<Unit> = safeApiCall {
        val response = client.post("rest/v1/profiles") {
            header("Prefer", "resolution=merge-duplicates,return=minimal")
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(profile)
        }
        if (!response.status.isSuccess()) {
            throw NetworkException("Could not save your profile (${response.status.value}).")
        }
        Unit
    }
}
