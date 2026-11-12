
package compose.project.sit_research_hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import compose.project.sit_research_hub.data.repository.SettingsRepositoryImpl
import compose.project.sit_research_hub.data.repository.StudyRepositoryImpl
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.di.platformPreferences
import compose.project.sit_research_hub.di.platformDatabase
import compose.project.sit_research_hub.domain.usecase.FilterStudiesUseCase
import compose.project.sit_research_hub.presentation.home.HomeViewModel
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel
import compose.project.sit_research_hub.presentation.navigation.AppNavHost
import compose.project.sit_research_hub.ui.theme.ResearchHubTheme

@Composable
fun App() {
    val settingsRepository = remember { SettingsRepositoryImpl(platformPreferences()) }
    val settingsViewModel = remember(settingsRepository) { SettingsViewModel(settingsRepository) }
    val homeViewModel = remember {
        HomeViewModel(StudyRepositoryImpl(platformDatabase()), settingsRepository, FilterStudiesUseCase())
    }
    val settings = settingsViewModel.settings.collectAsState().value
    ResearchHubTheme(darkTheme = settings.themeMode == ThemeMode.DARK) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        ) {
            AppNavHost(settingsViewModel, homeViewModel)
        }
    }
}
