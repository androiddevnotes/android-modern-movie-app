package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun <T> ItemListGridView(
  items: List<T>,
  viewModel: MovieViewModel,
  onItemClick: (T) -> Unit,
  viewType: String,
  searchQuery: String,
  itemContent: @Composable (T, Int, () -> Unit, () -> Unit) -> Unit,
) {
  val lastViewedItemIndex by viewModel.lastViewedItemIndex.collectAsState()
  val gridState =
    rememberForeverLazyStaggeredGridState(
      key = "item_grid_${viewType}_$searchQuery",
      initialFirstVisibleItemIndex = lastViewedItemIndex,
      initialFirstVisibleItemScrollOffset = 0,
    )

  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(3),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalItemSpacing = 8.dp,
    state = gridState,
  ) {
    itemsIndexed(
      items = items,
      key = { index, item -> "${item.hashCode()}_$index" },
    ) { index, item ->
      if (index >= items.size - 1 && !viewModel.isLastPage) {
        viewModel.loadMoreMovies()
      }
      itemContent(
        item,
        index,
        {
          viewModel.setLastViewedItemIndex(index)
          onItemClick(item)
        },
        {
        },
      )
    }
  }
}
