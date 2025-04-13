package com.example.tmdbapp.di

import com.example.tmdbapp.data.FavoritePreferencesDatastore
import com.example.tmdbapp.data.SessionManagerPreferencesDataStore
import com.example.tmdbapp.network.*
import com.example.tmdbapp.repository.Repository
import com.example.tmdbapp.ui.viewmodel.TmdbViewModel
import com.example.tmdbapp.utils.ApiKeyManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Network
    single { KtorClient.httpClient }
    single<TmdbApiService> { TmdbApiServiceImpl(get()) }
    single<OpenAiApiService> { OpenAiApiServiceImpl(get()) }
    
    // DataStore
    single { FavoritePreferencesDatastore(get()) }
    single { SessionManagerPreferencesDataStore(get()) }
    
    // Repository
    single { 
        Repository(
            context = get(),
            tmdbApi = get(),
            openAiApi = get(),
            favoritePreferencesDatastore = get(),
            sessionManagerPreferencesDataStore = get(),
            apiKeyManager = get(),
            tmdbApiKeyFlow = get<ApiKeyManager>().tmdbApiKeyFlow,
            openAiApiKeyFlow = get<ApiKeyManager>().openAiApiKeyFlow
        )
    }
    
    // API Key Manager
    single { ApiKeyManager(get()) }
    
    // ViewModels
    viewModel { 
        TmdbViewModel(
            application = get(),
            repository = get(),
            apiKeyManager = get(),
            sessionManagerPreferencesDataStore = get()
        )
    }
} 