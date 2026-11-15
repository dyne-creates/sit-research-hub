package compose.project.sit_research_hub.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class SubmissionApi(private val rest: RestClient) {
    suspend fun submit(studyId: String, answers: List<AnswerDto>) {
        rest.send(HttpMethod.Post, "rest/v1/rpc/submit_response") {
            contentType(ContentType.Application.Json); setBody(SubmitRequest(studyId, answers))
        }
    }
    suspend fun participations(): List<ParticipationDto> = rest.send(HttpMethod.Get, "rest/v1/participations") {
        parameter("select", "*"); parameter("order", "created_at.desc")
    }.body()
    suspend fun responses(studyId: String): List<ResponseDto> = rest.send(HttpMethod.Get, "rest/v1/responses") {
        parameter("study_id", "eq.$studyId"); parameter("select", "*"); parameter("order", "created_at.asc")
    }.body()
}
