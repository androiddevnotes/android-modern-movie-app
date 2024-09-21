package com.example.tmdbapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.ui.components.SimpleItemUi

@Composable
fun <T : Any> ItemListListView(
    items: List<T>,
    onItemClick: (T) -> Unit,
    listState: LazyListState,
    isLastPage: Boolean,
    loadMoreItems: () -> Unit,
    setLastViewedItemIndex: (Int) -> Unit,
    toggleFavorite: (T) -> Unit,
    getItemId: (T) -> Any,
    getItemTitle: (T) -> String,
    getItemOverview: (T) -> String,
    getItemPosterPath: (T) -> String?,
    getItemVoteAverage: (T) -> Float,
    isItemFavorite: (T) -> Boolean,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
    ) {
        itemsIndexed(
            items = items,
            key = { index, item -> "${getItemId(item)}_$index" },
        ) { index, item ->
            if (index >= items.size - 1 && !isLastPage) {
                loadMoreItems()
            }
            SimpleItemUi(
                title = getItemTitle(item),
                overview = getItemOverview(item),
                posterPath = getItemPosterPath(item),
                voteAverage = getItemVoteAverage(item),
                isFavorite = isItemFavorite(item),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setLastViewedItemIndex(index)
                        onItemClick(item)
                    },
                onFavoriteClick = {
                    toggleFavorite(item)
                },
            )
        }
    }
}