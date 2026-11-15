package compose.project.sit_research_hub.di
import compose.project.sit_research_hub.data.local.AppDatabase
import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.local.createAppDatabase
import compose.project.sit_research_hub.domain.repository.TextSharer
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
actual fun platformDatabase(): AppDatabase = createAppDatabase()
actual fun platformPreferences(): PreferencesStore = PreferencesStore()
actual fun platformTextSharer(): TextSharer = object : TextSharer {
    override val actionLabel = "Copy summary"
    override fun share(title: String, text: String): String {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection("$title\n\n$text"), null)
        return "Summary copied to the clipboard."
    }
}
