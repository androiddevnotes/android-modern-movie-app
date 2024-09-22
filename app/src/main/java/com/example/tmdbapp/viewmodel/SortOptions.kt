package com.example.tmdbapp.viewmodel

import androidx.annotation.StringRes
import com.example.tmdbapp.R

enum class SortOptions(
  val apiValue: String,
  @StringRes val stringRes: Int,
) {
  NOW_PLAYING("release_date.desc", R.string.sort_now_playing),
  POPULAR("popularity.desc", R.string.sort_popularity),
  TOP_RATED("vote_average.desc", R.string.sort_top_rated),
  UPCOMING("primary_release_date.asc", R.string.sort_upcoming),
}
