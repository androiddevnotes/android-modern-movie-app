package com.example.tmdbapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.utils.ImageDownloader
import kotlinx.coroutines.launch

fun AlphaViewModel.downloadImage(
  posterPath: String?,
  context: Context,
) {
  viewModelScope.launch {
    ImageDownloader.downloadImage(posterPath, context)
  }
}
