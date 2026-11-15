package compose.project.sit_research_hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import compose.project.sit_research_hub.domain.model.ThemeMode
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
import kotlinx.coroutines.launch
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.repository.SettingsRepository

@Composable
fun App(googleIdTokenProvider: GoogleIdTokenProvider) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.authState.collectAsState().value
    val authUiState = authViewModel.uiState.collectAsState().value
    val settingsRepository = koinInject<SettingsRepository>()
    val studyRepository = koinInject<StudyRepository>()
    val localDataCleaner = koinInject<compose.project.sit_research_hub.data.repository.RoomLocalDataCleaner>()
    val coroutineScope = rememberCoroutineScope()
    val settingsViewModel = remember(settingsRepository) { SettingsViewModel(settingsRepository) }
    val homeViewModel = remember(settingsRepository) {
        HomeViewModel(studyRepository, settingsRepository, FilterStudiesUseCase())
    }
    val settings = settingsViewModel.settings.collectAsState().value
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    ResearchHubTheme(darkTheme = darkTheme) {
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
                    LaunchedEffect(state.user.id, state.profile) {
                        homeViewModel.updateProfile(state.profile)
                        homeViewModel.refresh()
                    }
                    AppNavHost(settingsViewModel, homeViewModel, state.user, state.profile, {
                        coroutineScope.launch {
                            localDataCleaner.clear()
                            authViewModel.signOut()
                        }
                    })
                }
            }
        }
    }
}
