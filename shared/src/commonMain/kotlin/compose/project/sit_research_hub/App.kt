package compose.project.sit_research_hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import compose.project.sit_research_hub.data.repository.SettingsRepositoryImpl
import compose.project.sit_research_hub.data.repository.StudyRepositoryImpl
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.di.platformDatabase
import compose.project.sit_research_hub.domain.usecase.FilterStudiesUseCase
import compose.project.sit_research_hub.presentation.home.HomeViewModel
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel
import compose.project.sit_research_hub.presentation.navigation.AppNavHost
import compose.project.sit_research_hub.ui.theme.ResearchHubTheme
import compose.project.sit_research_hub.domain.model.AuthState
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.presentation.auth.AuthViewModel
import compose.project.sit_research_hub.presentation.auth.AuthUiState
import compose.project.sit_research_hub.ui.components.LoadingView
import compose.project.sit_research_hub.ui.screens.SignInScreen
import compose.project.sit_research_hub.ui.screens.ProfileSetupScreen
import compose.project.sit_research_hub.domain.repository.GoogleIdTokenProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun App(googleIdTokenProvider: GoogleIdTokenProvider) {
    val preferences = koinInject<compose.project.sit_research_hub.data.local.PreferencesStore>()
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.authState.collectAsState().value
    val authUiState = authViewModel.uiState.collectAsState().value
    val settingsRepository = remember(preferences) { SettingsRepositoryImpl(preferences) }
    val settingsViewModel = remember(settingsRepository) { SettingsViewModel(settingsRepository) }
    val homeViewModel = remember(settingsRepository) {
        HomeViewModel(StudyRepositoryImpl(platformDatabase()), settingsRepository, FilterStudiesUseCase())
    }
    val settings = settingsViewModel.settings.collectAsState().value
    ResearchHubTheme(darkTheme = settings.themeMode == ThemeMode.DARK) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        ) {
            when (val state = authState) {
                AuthState.Checking -> LoadingView()
                AuthState.SignedOut -> SignInScreen(
                    uiState = authUiState,
                    onSignIn = { authViewModel.signIn(googleIdTokenProvider) },
                    onSignUp = { authViewModel.signUp(googleIdTokenProvider) },
                    onCancel = authViewModel::cancelSignIn
                )
                is AuthState.NeedsProfile -> ProfileSetupScreen(
                    isSaving = authUiState is AuthUiState.Loading,
                    externalError = (authUiState as? AuthUiState.Error)?.message,
                    onContinue = { program, year ->
                        authViewModel.saveProfile(StudentProfile(program, year), consentAccepted = true)
                    }
                )
                is AuthState.SignedIn -> {
                    LaunchedEffect(state.profile) { homeViewModel.updateProfile(state.profile) }
                    AppNavHost(settingsViewModel, homeViewModel, state.user, authViewModel::signOut)
                }
            }
        }
    }
}
