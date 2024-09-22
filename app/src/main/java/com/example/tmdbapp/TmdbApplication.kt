package com.example.tmdbapp

import android.app.Application
import com.example.tmdbapp.utils.BetterTimberDebugTree
import timber.log.Timber

class TmdbApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(BetterTimberDebugTree())
    }
    Timber.d("Application onCreate")
  }
}
