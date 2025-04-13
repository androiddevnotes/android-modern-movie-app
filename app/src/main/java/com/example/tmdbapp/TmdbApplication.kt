package com.example.tmdbapp

import android.app.Application
import com.example.tmdbapp.di.appModule
import com.example.tmdbapp.utils.BetterTimberDebugTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

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
