package compose.project.sit_research_hub.data.remote

import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthException
import compose.project.sit_research_hub.domain.model.NetworkException
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthApi(private val client: HttpClient) {

    private fun normalizeSignupId(value: String): String {
        // Scalar UUID RPC responses can be quoted by PostgREST and then passed
        // through platform serializers. Strip any surrounding quote layer
        // before sending the UUID back as a named RPC parameter.
        val normalized = value.trim().trim('"').trim()
        val uuidPattern = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
        if (!uuidPattern.matches(normalized)) {
            throw AuthException("Could not validate the sign-up request. Please start signing up again.")
        }
        return normalized
    }

    suspend fun prepareGoogleSignIn(): AppResult<Unit> = safeApiCall {
        val response = client.post("rest/v1/rpc/prepare_google_signin")
        if (!response.status.isSuccess()) {
            throw NetworkException("Could not start sign-in securely. Please try again.")
        }
    }

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
        normalizeSignupId(response.body<String>())
    }

    suspend fun completeGoogleSignUp(accessToken: String, signupId: String): AppResult<Boolean> = safeApiCall {
        val response = client.post("rest/v1/rpc/complete_google_signup") {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(SignupIntentRequest(normalizeSignupId(signupId)))
        }
        if (!response.status.isSuccess()) {
            val serverMessage = runCatching {
                Json.parseToJsonElement(response.bodyAsText())
                    .jsonObject["message"]
                    ?.jsonPrimitive
                    ?.content
            }.getOrNull()
            val userMessage = when {
                serverMessage.orEmpty().contains("account already", ignoreCase = true) ||
                    serverMessage.orEmpty().contains("already registered", ignoreCase = true) ->
                    "You already have an account. Sign in instead."
                serverMessage.orEmpty().contains("sign up first", ignoreCase = true) ||
                    serverMessage.orEmpty().contains("signup intent", ignoreCase = true) ->
                    "Your sign-up request expired. Please try signing up again."
                serverMessage.isNullOrBlank() ->
                    "Could not complete account setup. Please try again."
                else ->
                    "Could not complete account setup: $serverMessage"
            }
            throw AuthException(userMessage)
        }
        response.body<Boolean>()
    }

    suspend fun cancelGoogleSignUp(signupId: String): AppResult<Unit> = safeApiCall {
        val response = client.post("rest/v1/rpc/cancel_google_signup") {
            contentType(ContentType.Application.Json)
            setBody(SignupIntentRequest(normalizeSignupId(signupId)))
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
