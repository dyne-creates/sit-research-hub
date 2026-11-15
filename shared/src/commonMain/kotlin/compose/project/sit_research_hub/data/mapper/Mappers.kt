package compose.project.sit_research_hub.data.mapper

import compose.project.sit_research_hub.data.local.StoredSession
import compose.project.sit_research_hub.data.remote.AuthSessionDto
import compose.project.sit_research_hub.data.remote.ProfileDto
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.User
import compose.project.sit_research_hub.data.local.StudyEntity
import compose.project.sit_research_hub.data.local.ParticipationEntity
import compose.project.sit_research_hub.data.remote.*
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.data.remote.appJson
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

fun AuthSessionDto.toStoredSession() = StoredSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    userId = user.id,
    email = user.email.orEmpty(),
    displayName = user.userMetadata?.fullName ?: user.userMetadata?.name
)

fun StoredSession.toUser() = User(id = userId, email = email, displayName = displayName)

fun ProfileDto.toDomain() = StudentProfile(program = program, yearLevel = "${yearLevel}${if (yearLevel == 1) "st" else if (yearLevel == 2) "nd" else if (yearLevel == 3) "rd" else "th"} Year")

private val questionListSerializer = ListSerializer(QuestionDto.serializer())
private val stringListSerializer = ListSerializer(String.serializer())
private val intListSerializer = ListSerializer(Int.serializer())
private fun studyType(value: String) = StudyType.entries.firstOrNull { it.name == value } ?: StudyType.QUANTITATIVE
private fun studyStatus(value: String) = StudyStatus.entries.firstOrNull { it.name == value } ?: StudyStatus.DRAFT

fun QuestionDto.toDomain() = Question(id, text, QuestionType.entries.firstOrNull { it.name == type } ?: QuestionType.SHORT_ANSWER, required, options, ratingMax)
fun Question.toDto() = QuestionDto(id, text.trim(), type.name, isRequired, options.map(String::trim), ratingMax)

fun StudyDto.toDomain() = Study(
    id, ownerId, ownerName, title, description, studyType(studyType),
    Eligibility(eligiblePrograms, eligibleYearLevels, requirements), isAnonymous, oneResponse,
    studyStatus(status), questions.map { it.toDomain() }, createdAt
)

fun Study.toNewDto() = NewStudyDto(
    ownerId, ownerName, title.trim(), description.trim(), type.name, eligibility.description.trim(),
    eligibility.programs, eligibility.yearLevels, isAnonymous, oneResponsePerRespondent, status.name,
    questions.map { it.toDto() }
)

fun StudyDto.toEntity() = StudyEntity(
    id, ownerId, ownerName, title, description, studyType, requirements,
    appJson.encodeToString(stringListSerializer, eligiblePrograms),
    appJson.encodeToString(intListSerializer, eligibleYearLevels), isAnonymous, oneResponse, status,
    appJson.encodeToString(questionListSerializer, questions), createdAt
)

fun StudyEntity.toDomain() = Study(
    id, ownerId, ownerName, title, description, studyType(studyType),
    Eligibility(appJson.decodeFromString(stringListSerializer, programsJson), appJson.decodeFromString(intListSerializer, yearLevelsJson), requirements),
    isAnonymous, oneResponse, studyStatus(status), appJson.decodeFromString(questionListSerializer, questionsJson).map { it.toDomain() }, createdAt
)

fun ParticipationDto.toEntity() = ParticipationEntity(id, studyId, studyTitle, researcherName, createdAt)
fun ParticipationEntity.toDomain() = Participation(studyId, studyTitle, researcherName, createdAt)
fun Answer.toDto() = AnswerDto(questionId, values)
fun ResponseDto.toDomain() = StudyResponse(id, respondentName, answers.map { Answer(it.questionId, it.values) }, createdAt)
fun SlotDto.toDomain() = InterviewSlot(id, studyId, startsAt, location, notes, bookedBy, bookedByName)
