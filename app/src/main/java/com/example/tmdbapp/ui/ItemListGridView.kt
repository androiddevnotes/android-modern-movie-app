package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun <T> ItemListGridView(
  items: List<T>,
  viewModel: MovieViewModel,
  onItemClick: (T) -> Unit,
  viewType: String,
  searchQuery: String,
  gridState: LazyStaggeredGridState,
  itemContent: @Composable (T, Int, () -> Unit, () -> Unit) -> Unit,
) {
  val lastViewedItemIndex by viewModel.lastViewedItemIndex.collectAsState()
  var shouldScroll by remember { mutableStateOf(false) }

  LaunchedEffect(lastViewedItemIndex) {
    if (lastViewedItemIndex > 0) {
      shouldScroll = true
    }
  }

  LaunchedEffect(shouldScroll) {
    if (shouldScroll) {
      gridState.scrollToItem(lastViewedItemIndex)
      viewModel.setLastViewedItemIndex(0)
      shouldScroll = false
    }
  }

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
        { onItemClick(item) },
        { /* You can add long click behavior here if needed */ },
      )
    }
  }
}
