package compose.project.sit_research_hub

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "sit-research-hub",
    ) {
        App()
    }
}