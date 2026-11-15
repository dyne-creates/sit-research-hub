package compose.project.sit_research_hub.di
import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.local.createAppDatabase
import compose.project.sit_research_hub.data.local.AndroidPlatformContext
import compose.project.sit_research_hub.domain.repository.TextSharer
import android.content.Intent
actual fun platformDatabase(): AppDatabase = createAppDatabase()
actual fun platformPreferences(): PreferencesStore = PreferencesStore()
actual fun platformTextSharer(): TextSharer = object : TextSharer {
    override val actionLabel = "Share summary"
    override fun share(title: String, text: String): String {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AndroidPlatformContext.application.startActivity(Intent.createChooser(send, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Choose an app to share the summary."
    }
}
