
package compose.project.sit_research_hub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.project.sit_research_hub.ui.screens.HomeScreen
import compose.project.sit_research_hub.ui.screens.SettingsScreen
import compose.project.sit_research_hub.ui.screens.StudyDetailScreen
import compose.project.sit_research_hub.ui.screens.YourResearchesScreen
import compose.project.sit_research_hub.ui.screens.AddResearchScreen
import compose.project.sit_research_hub.ui.screens.ResearchSummaryScreen
import compose.project.sit_research_hub.ui.screens.MyResponsesScreen
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel
import compose.project.sit_research_hub.presentation.home.HomeViewModel
import compose.project.sit_research_hub.domain.model.User
import compose.project.sit_research_hub.domain.model.StudentProfile

@Composable
fun AppNavHost(
    settingsViewModel: SettingsViewModel,
    homeViewModel: HomeViewModel,
    user: User,
    profile: StudentProfile,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    var selectedResearchId by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Destination.HOME
    ) {
        // Home / Research Hub Screen
        composable(Destination.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onResearchClick = { researchId ->
                    selectedResearchId = researchId
                    navController.navigate(
                        Destination.researchDetails(researchId)
                    )
                },
                onCreateResearch = { navController.navigate(Destination.ADD_RESEARCH) },
                onYourResearchesClick = { navController.navigate(Destination.YOUR_RESEARCHES) },
                onSettingsClick = {
                    navController.navigate(Destination.SETTINGS)
                }
            )
        }

        composable(Destination.ADD_RESEARCH) {
            AddResearchScreen(user.id, user.displayName ?: user.email, onBack = { navController.popBackStack() })
        }

        composable(Destination.YOUR_RESEARCHES) {
            YourResearchesScreen(user.id, onBack = { navController.popBackStack() }, onStudyClick = { id ->
                selectedResearchId = id
                navController.navigate(Destination.researchDetails(id))
            }, onResultsClick = { id ->
                selectedResearchId = id
                navController.navigate(Destination.RESEARCH_SUMMARY)
            })
        }

        composable(Destination.RESEARCH_SUMMARY) {
            ResearchSummaryScreen(selectedResearchId, onBack = { navController.popBackStack() })
        }

        composable(Destination.MY_RESPONSES) {
            MyResponsesScreen(onBack = { navController.popBackStack() })
        }

        // Research Details Screen
        // Temporary: displays research record 1
        composable(Destination.RESEARCH_DETAILS) { backStackEntry ->
            StudyDetailScreen(
                researchId = selectedResearchId,
                userId = user.id,
                profile = profile,
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
                onMyResponses = { navController.navigate(Destination.MY_RESPONSES) },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
