package compose.project.sit_research_hub.di

import compose.project.sit_research_hub.data.local.PreferencesStore
import compose.project.sit_research_hub.data.remote.AuthApi
import compose.project.sit_research_hub.data.remote.createHttpClient
import compose.project.sit_research_hub.data.repository.AuthRepositoryImpl
import compose.project.sit_research_hub.domain.repository.AuthRepository
import compose.project.sit_research_hub.presentation.auth.AuthViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun appModule(config: AppConfig) = module {
    single { config }
    single { createHttpClient(get()) }
    single { platformPreferences() }
    single { AuthApi(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
}

fun initKoin(config: AppConfig, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule(config))
}
