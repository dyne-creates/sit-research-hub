
package compose.project.sit_research_hub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.project.sit_research_hub.ui.screens.HomeScreen
import compose.project.sit_research_hub.ui.screens.ProfileSetupScreen
import compose.project.sit_research_hub.ui.screens.SettingsScreen
import compose.project.sit_research_hub.ui.screens.StudyDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.PERSONALIZATION
    ) {
        // Welcome / Personalization Screen
        composable(Destination.PERSONALIZATION) {
            ProfileSetupScreen(
                onContinue = {
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
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
