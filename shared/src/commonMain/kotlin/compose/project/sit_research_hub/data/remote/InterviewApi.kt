package compose.project.sit_research_hub.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class InterviewApi(private val rest: RestClient) {
    suspend fun slots(studyId: String): List<SlotDto> = rest.send(HttpMethod.Get, "rest/v1/interview_slots") {
        parameter("study_id", "eq.$studyId"); parameter("select", "*"); parameter("order", "starts_at.asc")
    }.body()
    suspend fun bookedSlots(userId: String): List<SlotDto> = rest.send(HttpMethod.Get, "rest/v1/interview_slots") {
        parameter("booked_by", "eq.$userId"); parameter("select", "*"); parameter("order", "starts_at.asc")
    }.body()
    suspend fun create(dto: NewSlotDto): SlotDto = rest.send(HttpMethod.Post, "rest/v1/interview_slots") {
        header("Prefer", "return=representation"); contentType(ContentType.Application.Json); setBody(dto)
    }.body<List<SlotDto>>().first()
    suspend fun delete(slotId: String) { rest.send(HttpMethod.Delete, "rest/v1/interview_slots") { parameter("id", "eq.$slotId") } }
    suspend fun book(slotId: String) {
        rest.send(HttpMethod.Post, "rest/v1/rpc/book_interview_slot") {
            contentType(ContentType.Application.Json); setBody(BookSlotRequest(slotId))
        }
    }
}
