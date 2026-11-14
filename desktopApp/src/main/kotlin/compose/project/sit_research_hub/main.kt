package compose.project.sit_research_hub

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import compose.project.sit_research_hub.di.AppConfig
import compose.project.sit_research_hub.di.initKoin
import java.io.File
import java.util.Properties

private fun setting(name: String): String {
    System.getProperty(name)?.takeIf(String::isNotBlank)?.let { return it }
    System.getenv(name)?.takeIf(String::isNotBlank)?.let { return it }
    var directory: File? = File("").absoluteFile
    repeat(4) {
        val file = File(directory, "local.properties")
        if (file.isFile) {
            val value = Properties().apply { file.inputStream().use { input -> load(input) } }.getProperty(name)
            if (!value.isNullOrBlank()) return value
        }
        directory = directory?.parentFile
    }
    return ""
}

fun main() = application {
    val config = AppConfig(
        supabaseUrl = setting("SUPABASE_URL"),
        supabaseAnonKey = setting("SUPABASE_ANON_KEY"),
        googleWebClientId = setting("GOOGLE_WEB_CLIENT_ID"),
        allowedEmailDomain = setting("ALLOWED_EMAIL_DOMAIN").ifEmpty { "s.ubaguio.edu" }
    )
    initKoin(config)
    val googleProvider = DesktopGoogleSignInProvider(config.supabaseUrl, config.allowedEmailDomain)
    Window(
        onCloseRequest = ::exitApplication,
        title = "sit-research-hub",
    ) {
        App(googleProvider)
    }
}
