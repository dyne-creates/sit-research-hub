package compose.project.sit_research_hub.data.mapper

import compose.project.sit_research_hub.data.local.StoredSession
import compose.project.sit_research_hub.data.remote.AuthSessionDto
import compose.project.sit_research_hub.data.remote.ProfileDto
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.User

fun AuthSessionDto.toStoredSession() = StoredSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    userId = user.id,
    email = user.email.orEmpty(),
    displayName = user.userMetadata?.fullName ?: user.userMetadata?.name
)

fun StoredSession.toUser() = User(id = userId, email = email, displayName = displayName)

fun ProfileDto.toDomain() = StudentProfile(program = program, yearLevel = "${yearLevel}${if (yearLevel == 1) "st" else if (yearLevel == 2) "nd" else if (yearLevel == 3) "rd" else "th"} Year")
