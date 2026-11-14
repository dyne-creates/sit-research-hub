package compose.project.sit_research_hub.data.remote

import kotlinx.serialization.*

@Serializable
data class AccountExistsRequest(@SerialName("p_email") val email: String)

@Serializable
data class SignupIntentRequest(@SerialName("p_signup_id") val signupId: String)

@Serializable
data class IdTokenRequest(
    val provider: String = "google",
    @SerialName("id_token") val idToken: String,
    val nonce: String? = null
)

@Serializable
data class PkceTokenRequest(
    @SerialName("auth_code") val authCode: String,
    @SerialName("code_verifier") val codeVerifier: String,
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class UserMetadataDto(
    @SerialName("full_name") val fullName: String? = null,
    val name: String? = null
)

@Serializable
data class SupabaseUserDto(
    val id: String,
    val email: String? = null,
    @SerialName("user_metadata") val userMetadata: UserMetadataDto? = null
)

@Serializable
data class AuthSessionDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    val user: SupabaseUserDto
)

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    @SerialName("full_name") val fullName: String? = null,
    val program: String,
    @SerialName("year_level") val yearLevel: Int
)
