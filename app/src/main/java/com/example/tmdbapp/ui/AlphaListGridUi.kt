package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.ui.components.*

@Composable
fun <T : Any> AlphaListGridUi(
  items: List<T>,
  onItemClick: (T) -> Unit,
  gridState: LazyStaggeredGridState,
  isLastPage: Boolean,
  loadMoreItems: () -> Unit,
  setLastViewedItemIndex: (Int) -> Unit,
  toggleFavorite: (T) -> Unit,
  getItemId: (T) -> Any,
  getItemTitle: (T) -> String,
  getItemPosterPath: (T) -> String?,
  getItemVoteAverage: (T) -> Float,
  isItemFavorite: (T) -> Boolean,
) {
  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(3),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalItemSpacing = 8.dp,
    state = gridState,
  ) {
    itemsIndexed(
      items = items,
      key = { index, item -> "${getItemId(item)}_$index" },
    ) { index, item ->
      if (index >= items.size - 1 && !isLastPage) {
        loadMoreItems()
      }
      GridItemUi(
        title = getItemTitle(item),
        posterPath = getItemPosterPath(item),
        voteAverage = getItemVoteAverage(item),
        isFavorite = isItemFavorite(item),
        onClick = {
          setLastViewedItemIndex(index)
          onItemClick(item)
        },
        onLongClick = {
          toggleFavorite(item)
        },
      )
    }
  }
}
