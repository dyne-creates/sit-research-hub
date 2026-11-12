
package compose.project.sit_research_hub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.project.sit_research_hub.ui.screens.HomeScreen
import compose.project.sit_research_hub.ui.screens.ProfileSetupScreen
import compose.project.sit_research_hub.ui.screens.SettingsScreen
import compose.project.sit_research_hub.ui.screens.StudyDetailScreen
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel
import compose.project.sit_research_hub.presentation.home.HomeViewModel
import compose.project.sit_research_hub.domain.model.StudentProfile

@Composable
fun AppNavHost(settingsViewModel: SettingsViewModel, homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val settings by settingsViewModel.settings.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Destination.PERSONALIZATION
    ) {
        // Welcome / Personalization Screen
        composable(Destination.PERSONALIZATION) {
            ProfileSetupScreen(
                onContinue = { program, yearLevel ->
                    homeViewModel.updateProfile(StudentProfile(program, yearLevel))
                    navController.navigate(Destination.HOME) {
                        popUpTo(Destination.PERSONALIZATION) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Home / Research Hub Screen
        composable(Destination.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onResearchClick = { researchId ->
                    navController.navigate(
                        Destination.researchDetails(researchId)
                    )
                },
                onSettingsClick = {
                    navController.navigate(Destination.SETTINGS)
                }
            )
        }

        // Research Details Screen
        // Temporary: displays research record 1
        composable(Destination.RESEARCH_DETAILS) {
            StudyDetailScreen(
                researchId = "1",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Settings Screen
        composable(Destination.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
