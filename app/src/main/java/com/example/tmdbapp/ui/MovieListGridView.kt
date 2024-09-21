package com.example.tmdbapp.ui

import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.viewmodel.*

@Composable
fun MovieListGridView(
  movies: List<Movie>,
  viewModel: MovieViewModel,
  onMovieClick: (Movie) -> Unit,
  gridState: LazyStaggeredGridState,
) {
  ItemListGridView(
    items = movies,
    viewModel = viewModel,
    onItemClick = onMovieClick,
    gridState = gridState,
    lastViewedItemIndex = viewModel.lastViewedItemIndex,
    setLastViewedItemIndex = viewModel::setLastViewedItemIndex,
    loadMoreItems = viewModel::loadMoreMovies,
    isLastPage = viewModel.isLastPage,
  ) { movie, index, onClick, onLongClick ->
    GridItemUi(
      title = movie.title,
      posterPath = movie.posterPath,
      voteAverage = movie.voteAverage,
      isFavorite = movie.isFavorite,
      onClick = {
        viewModel.setLastViewedItemIndex(index)
        onClick()
      },
      onLongClick = {
        viewModel.toggleFavorite(movie)
        onLongClick()
      },
    )
  }
}
