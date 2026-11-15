package compose.project.sit_research_hub.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class StudyApi(private val rest: RestClient) {
    suspend fun list(): List<StudyDto> = rest.send(HttpMethod.Get, "rest/v1/studies") {
        parameter("select", "*"); parameter("order", "created_at.desc")
    }.body()

    suspend fun get(id: String): StudyDto? = rest.send(HttpMethod.Get, "rest/v1/studies") {
        parameter("id", "eq.$id"); parameter("select", "*")
    }.body<List<StudyDto>>().firstOrNull()

    suspend fun create(dto: NewStudyDto): StudyDto = rest.send(HttpMethod.Post, "rest/v1/studies") {
        header("Prefer", "return=representation"); contentType(ContentType.Application.Json); setBody(dto)
    }.body<List<StudyDto>>().first()

    suspend fun setStatus(id: String, status: String): StudyDto? = rest.send(HttpMethod.Patch, "rest/v1/studies") {
        parameter("id", "eq.$id"); header("Prefer", "return=representation")
        contentType(ContentType.Application.Json); setBody(StatusPatch(status))
    }.body<List<StudyDto>>().firstOrNull()
}
