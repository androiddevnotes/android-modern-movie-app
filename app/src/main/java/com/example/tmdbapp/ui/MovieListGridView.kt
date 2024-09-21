package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.MovieGridItem
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MovieListGridView(
  movies: List<Movie>,
  viewModel: MovieViewModel,
  onMovieClick: (Movie) -> Unit,
  viewType: String,
  searchQuery: String,
) {
  val lastViewedItemIndex by viewModel.lastViewedItemIndex.collectAsState()
  val gridState =
    rememberForeverLazyStaggeredGridState(
      key = "movie_grid_${viewType}_$searchQuery",
      initialFirstVisibleItemIndex = lastViewedItemIndex,
      initialFirstVisibleItemOffset = 0,
    )

  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(3),
    contentPadding = PaddingValues(4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalItemSpacing = 4.dp,
    state = gridState,
  ) {
    itemsIndexed(
      items = movies,
      key = { index, movie -> "${movie.id}_$index" },
    ) { index, movie ->
      if (index >= movies.size - 1 && !viewModel.isLastPage) {
        viewModel.loadMoreMovies()
      }
      MovieGridItem(
        movie = movie,
        onClick = {
          viewModel.setLastViewedItemIndex(index)
          onMovieClick(movie)
        },
        onLongClick = {
          viewModel.toggleFavorite(movie)
        },
        isFavorite = viewModel.isFavorite(movie.id),
      )
    }
  }
}
