
package compose.project.sit_research_hub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.project.sit_research_hub.ui.screens.HomeScreen
import compose.project.sit_research_hub.ui.screens.SettingsScreen
import compose.project.sit_research_hub.ui.screens.StudyDetailScreen
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel
import compose.project.sit_research_hub.presentation.home.HomeViewModel
import compose.project.sit_research_hub.domain.model.User

@Composable
fun AppNavHost(
    settingsViewModel: SettingsViewModel,
    homeViewModel: HomeViewModel,
    user: User,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.HOME
    ) {
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
                accountEmail = user.email,
                onSignOut = onSignOut,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
