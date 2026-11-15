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

@Serializable data class QuestionDto(
    val id: String, val text: String, val type: String, val required: Boolean = true,
    val options: List<String> = emptyList(), @SerialName("rating_max") val ratingMax: Int = 5
)

@Serializable data class StudyDto(
    val id: String, @SerialName("owner_id") val ownerId: String, @SerialName("owner_name") val ownerName: String,
    val title: String, val description: String, @SerialName("study_type") val studyType: String,
    val requirements: String = "", @SerialName("eligible_programs") val eligiblePrograms: List<String> = emptyList(),
    @SerialName("eligible_year_levels") val eligibleYearLevels: List<Int> = emptyList(),
    @SerialName("is_anonymous") val isAnonymous: Boolean = false,
    @SerialName("one_response_per_respondent") val oneResponse: Boolean = true,
    val status: String = "DRAFT", val questions: List<QuestionDto> = emptyList(),
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable data class NewStudyDto(
    @SerialName("owner_id") val ownerId: String, @SerialName("owner_name") val ownerName: String,
    val title: String, val description: String, @SerialName("study_type") val studyType: String,
    val requirements: String, @SerialName("eligible_programs") val eligiblePrograms: List<String>,
    @SerialName("eligible_year_levels") val eligibleYearLevels: List<Int>,
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    @SerialName("one_response_per_respondent") val oneResponse: Boolean,
    val status: String, val questions: List<QuestionDto>
)

@Serializable data class StatusPatch(val status: String)
@Serializable data class AnswerDto(@SerialName("question_id") val questionId: String, val values: List<String>)
@Serializable data class SubmitRequest(@SerialName("p_study_id") val studyId: String, @SerialName("p_answers") val answers: List<AnswerDto>)
@Serializable data class ResponseDto(
    val id: String, @SerialName("respondent_name") val respondentName: String? = null,
    val answers: List<AnswerDto> = emptyList(), @SerialName("created_at") val createdAt: String = ""
)
@Serializable data class ParticipationDto(
    val id: String, @SerialName("study_id") val studyId: String,
    @SerialName("study_title") val studyTitle: String, @SerialName("researcher_name") val researcherName: String,
    @SerialName("created_at") val createdAt: String = ""
)
@Serializable data class SlotDto(
    val id: String, @SerialName("study_id") val studyId: String, @SerialName("starts_at") val startsAt: String,
    val location: String, val notes: String = "", @SerialName("booked_by") val bookedBy: String? = null,
    @SerialName("booked_by_name") val bookedByName: String? = null
)
@Serializable data class NewSlotDto(
    @SerialName("study_id") val studyId: String, @SerialName("starts_at") val startsAt: String,
    val location: String, val notes: String
)
@Serializable data class BookSlotRequest(@SerialName("p_slot_id") val slotId: String)
