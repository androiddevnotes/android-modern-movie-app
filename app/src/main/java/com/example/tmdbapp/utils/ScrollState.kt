package com.example.tmdbapp.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberForeverLazyListState(
    key: String,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    return rememberSaveable(key = key, saver = LazyListState.Saver) {
        LazyListState(
            firstVisibleItemIndex = initialFirstVisibleItemIndex,
            firstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset
        )
    }
}

@Composable
fun rememberForeverLazyStaggeredGridState(
    key: String,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemOffset: Int = 0
): LazyStaggeredGridState {
    return rememberSaveable(key = key, saver = LazyStaggeredGridState.Saver) {
        LazyStaggeredGridState(
            initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
            initialFirstVisibleItemOffset = initialFirstVisibleItemOffset
        )
    }
}