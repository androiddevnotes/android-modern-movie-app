package com.example.tmdbapp.models

import com.example.tmdbapp.R

sealed class BottomNavItem(
  val route: String,
  val label: String,
  val icon: Int,
) {
  data object Discover : BottomNavItem("movieList", "Discover", R.drawable.cool_shape_movie)

  data object Favorites : BottomNavItem("favorites", "Favorites", R.drawable.cool_shape_fav)

  data object CreateList : BottomNavItem("createList", "Create List", R.drawable.cool_shape_plus)
}
