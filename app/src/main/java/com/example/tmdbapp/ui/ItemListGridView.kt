package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T, VM : ViewModel> ItemListGridView(
  items: List<T>,
  viewModel: VM,
  onItemClick: (T) -> Unit,
  gridState: LazyStaggeredGridState,
  lastViewedItemIndex: StateFlow<Int>,
  setLastViewedItemIndex: (Int) -> Unit,
  loadMoreItems: () -> Unit,
  isLastPage: Boolean,
  itemContent: @Composable (T, Int, () -> Unit, () -> Unit) -> Unit,
) {
  val lastViewedItemIndexValue by lastViewedItemIndex.collectAsState()
  var shouldScroll by remember { mutableStateOf(false) }

  LaunchedEffect(lastViewedItemIndexValue) {
    if (lastViewedItemIndexValue > 0) {
      shouldScroll = true
    }
  }

  LaunchedEffect(shouldScroll) {
    if (shouldScroll) {
      gridState.scrollToItem(lastViewedItemIndexValue)
      setLastViewedItemIndex(0)
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
      if (index >= items.size - 1 && !isLastPage) {
        loadMoreItems()
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
