package compose.project.sit_research_hub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ResearchBlue = Color(0xFF263A63)
val ResearchLightBlue = Color(0xFF4776B4)
val ResearchPaleBlue = Color(0xFFEAF0FA)
val ResearchBackground = Color(0xFFF4F7FB)

private val LightColors = lightColorScheme(
    primary = ResearchBlue,
    secondary = ResearchLightBlue,
    tertiary = ResearchPaleBlue,
    background = ResearchBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EEF7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF172033),
    onSurface = Color(0xFF172033)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9C5EE),
    secondary = Color(0xFF86A9D4),
    tertiary = Color(0xFF304766),
    background = Color(0xFF0D1624),
    surface = Color(0xFF172337),
    surfaceVariant = Color(0xFF22334B),
    onPrimary = Color(0xFF101B2B),
    onSecondary = Color(0xFF07111F),
    onBackground = Color(0xFFE8EEF7),
    onSurface = Color(0xFFE8EEF7),
    onSurfaceVariant = Color(0xFFB9C7DA),
    outline = Color(0xFF71839C),
    outlineVariant = Color(0xFF3B4C64)
)

@Composable
fun ResearchHubTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}

