package compose.project.sit_research_hub.di
import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.local.createAppDatabase
import compose.project.sit_research_hub.domain.repository.TextSharer
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
actual fun platformDatabase(): AppDatabase = createAppDatabase()
actual fun platformPreferences(): PreferencesStore = PreferencesStore()
actual fun platformTextSharer(): TextSharer = object : TextSharer {
    override val actionLabel = "Share summary"
    @Suppress("DEPRECATION")
    override fun share(title: String, text: String): String {
        val controller = UIActivityViewController(listOf("$title\n\n$text"), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(controller, animated = true, completion = null)
        return "Choose an app to share the summary."
    }
}
