package com.example.tmdbapp.di

import com.example.tmdbapp.ui.viewmodel.AlphaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { 
        AlphaViewModel(
            application = get(),
            repository = get(),
            apiKeyManager = get(),
            sessionManagerPreferencesDataStore = get()
        )
    }
} 