package com.example.tmdbapp.utils

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*

@Composable
fun rememberForeverLazyListState(
  key: String,
  initialFirstVisibleItemIndex: Int = 0,
  initialFirstVisibleItemScrollOffset: Int = 0,
): LazyListState =
  rememberSaveable(key = key, saver = LazyListState.Saver) {
    LazyListState(
      firstVisibleItemIndex = initialFirstVisibleItemIndex,
      firstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
    )
  }

@Composable
fun rememberForeverLazyStaggeredGridState(
  key: String,
  initialFirstVisibleItemIndex: Int = 0,
  initialFirstVisibleItemOffset: Int = 0,
): LazyStaggeredGridState =
  rememberSaveable(key = key, saver = LazyStaggeredGridState.Saver) {
    LazyStaggeredGridState(
      initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
      initialFirstVisibleItemOffset = initialFirstVisibleItemOffset,
    )
  }
