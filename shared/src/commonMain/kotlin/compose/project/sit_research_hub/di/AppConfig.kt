package compose.project.sit_research_hub.di

data class AppConfig(
    val supabaseUrl: String,
    val supabaseAnonKey: String,
    val googleWebClientId: String,
    val allowedEmailDomain: String
) {
    fun isAllowedEmail(email: String?): Boolean = email?.trim()?.lowercase()?.endsWith("@" + allowedEmailDomain.trim().lowercase()) == true
}
