package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
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

@Composable
fun BottomNavigationBar(navController: NavController) {
  val items =
    listOf(
      BottomNavItem.Discover,
      BottomNavItem.Favorites,
      BottomNavItem.CreateList,
    )
  val navBackStackEntry = navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry.value?.destination?.route

  NavigationBar {
    items.forEach { item ->
      NavigationBarItem(
        icon = {
          Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp), // Adjust the size here
          )
        },
        label = { Text(item.label) },
        selected = currentRoute == item.route,
        onClick = {
          navController.navigate(item.route) {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
            launchSingleTop = true
            restoreState = true
          }
        },
      )
    }
  }
}
