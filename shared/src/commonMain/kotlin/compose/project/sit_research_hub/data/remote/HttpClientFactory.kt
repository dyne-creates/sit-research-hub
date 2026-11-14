package compose.project.sit_research_hub.data.remote

import compose.project.sit_research_hub.di.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val appJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}

fun createHttpClient(config: AppConfig): HttpClient = HttpClient {
    install(ContentNegotiation) { json(appJson) }
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 15_000
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.INFO
        sanitizeHeader { it == HttpHeaders.Authorization || it == "apikey" }
    }
    defaultRequest {
        url(config.supabaseUrl.trimEnd('/') + "/")
        header("apikey", config.supabaseAnonKey)
    }
}