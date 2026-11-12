
package compose.project.sit_research_hub

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import compose.project.sit_research_hub.presentation.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}
