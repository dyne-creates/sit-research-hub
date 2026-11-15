package compose.project.sit_research_hub.di

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.remote.AuthApi
import compose.project.sit_research_hub.data.remote.SessionManager
import compose.project.sit_research_hub.data.remote.RestClient
import compose.project.sit_research_hub.data.remote.StudyApi
import compose.project.sit_research_hub.data.remote.SubmissionApi
import compose.project.sit_research_hub.data.remote.InterviewApi
import compose.project.sit_research_hub.data.remote.createHttpClient
import compose.project.sit_research_hub.data.repository.AuthRepositoryImpl
import compose.project.sit_research_hub.data.repository.StudyRepositoryImpl
import compose.project.sit_research_hub.data.repository.SubmissionRepositoryImpl
import compose.project.sit_research_hub.data.repository.InterviewRepositoryImpl
import compose.project.sit_research_hub.data.repository.SettingsRepositoryImpl
import compose.project.sit_research_hub.data.repository.RoomLocalDataCleaner
import compose.project.sit_research_hub.domain.repository.AuthRepository
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
import compose.project.sit_research_hub.domain.repository.InterviewRepository
import compose.project.sit_research_hub.domain.repository.SettingsRepository
import compose.project.sit_research_hub.domain.usecase.CheckEligibilityUseCase
import compose.project.sit_research_hub.domain.usecase.FilterStudiesUseCase
import compose.project.sit_research_hub.domain.usecase.ValidateAnswersUseCase
import compose.project.sit_research_hub.domain.usecase.SubmitResponseUseCase
import compose.project.sit_research_hub.domain.usecase.ValidateStudyUseCase
import compose.project.sit_research_hub.domain.usecase.CreateInterviewSlotUseCase
import compose.project.sit_research_hub.domain.usecase.ChooseInterviewSlotUseCase
import compose.project.sit_research_hub.domain.usecase.ComputeResultsUseCase
import compose.project.sit_research_hub.presentation.auth.AuthViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun appModule(config: AppConfig) = module {
    single { config }
    single { createHttpClient(get()) }
    single { platformPreferences() }
    single { platformTextSharer() }
    single { AuthApi(get()) }
    single { SessionManager(get(), get()) }
    single { RestClient(get(), get()) }
    single { StudyApi(get()) }
    single { SubmissionApi(get()) }
    single { InterviewApi(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single { platformDatabase() }
    single { get<compose.project.sit_research_hub.data.local.AppDatabase>().studyDao() }
    single { get<compose.project.sit_research_hub.data.local.AppDatabase>().participationDao() }
    single { RoomLocalDataCleaner(get(), get()) }
    single<StudyRepository> { StudyRepositoryImpl(get(), get()) }
    single<SubmissionRepository> { SubmissionRepositoryImpl(get(), get()) }
    single<InterviewRepository> { InterviewRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single { CheckEligibilityUseCase() }
    single { FilterStudiesUseCase(get()) }
    single { ValidateAnswersUseCase() }
    single { SubmitResponseUseCase(get(), get(), get()) }
    single { ValidateStudyUseCase() }
    single { CreateInterviewSlotUseCase(get()) }
    single { ChooseInterviewSlotUseCase(get()) }
    single { ComputeResultsUseCase() }
    viewModel { AuthViewModel(get()) }
}

fun initKoin(config: AppConfig, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule(config))
}
