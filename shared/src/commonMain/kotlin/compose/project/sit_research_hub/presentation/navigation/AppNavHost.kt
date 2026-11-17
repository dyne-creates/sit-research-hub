
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
import compose.project.sit_research_hub.ui.screens.AccountScreen
import compose.project.sit_research_hub.ui.screens.InterviewSchedulesScreen
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
    onSignOut: () -> Unit,
    onSaveProfile: (StudentProfile) -> Unit
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
                onProfileClick = { navController.navigate(Destination.ACCOUNT) },
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
            YourResearchesScreen(user.id, onBack = { navController.navigate(Destination.HOME) }, onAddResearch = { navController.navigate(Destination.ADD_RESEARCH) }, onSettingsClick = { navController.navigate(Destination.SETTINGS) }, onStudyClick = { id ->
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

        composable(Destination.INTERVIEW_SCHEDULES) {
            InterviewSchedulesScreen(user.id, onBack = { navController.popBackStack() })
        }

        composable(Destination.ACCOUNT) {
            AccountScreen(
                user = user,
                profile = profile,
                // My Account is a destination reached from the app chrome. Do
                // not pop blindly: an empty restored back stack would leave the
                // NavHost with no visible destination. Returning always lands
                // on the stable signed-in Home destination.
                onBack = {
                    navController.navigate(Destination.HOME) {
                        popUpTo(Destination.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSaveProfile = onSaveProfile,
                onMyResponses = { navController.navigate(Destination.MY_RESPONSES) },
                onInterviewSchedules = { navController.navigate(Destination.INTERVIEW_SCHEDULES) },
                onSignOut = onSignOut
            )
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
                onBackClick = {
                    navController.navigate(Destination.HOME)
                },
                onYourResearches = { navController.navigate(Destination.YOUR_RESEARCHES) }
            )
        }
    }
}
