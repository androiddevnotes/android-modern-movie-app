package com.example.tmdbapp.viewmodel

interface BaseViewModel<T> {
  val isLastPage: Boolean

  fun loadMoreItems()

  fun setLastViewedItemIndex(index: Int)

  fun toggleFavorite(item: T)

  fun refreshItems()

  fun setFilterOptions(options: FilterOptions)

  fun setSearchQuery(query: String)

  fun setSortOption(sortOption: SortOption)
}
