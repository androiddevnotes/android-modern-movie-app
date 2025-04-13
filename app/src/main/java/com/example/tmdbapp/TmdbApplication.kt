package com.example.tmdbapp

import android.app.Application
import com.example.tmdbapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TmdbApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(BetterTimberDebugTree())
    }

    startKoin {
      androidLogger()
      androidContext(this@TmdbApplication)
      modules(appModule)
    }
  }
}
