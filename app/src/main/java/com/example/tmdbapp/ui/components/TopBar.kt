package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.R
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchIconClick: () -> Unit,
    onCloseSearchClick: () -> Unit,
    screenTitle: String,
    expandedDropdown: Boolean,
    onSortOptionClick: (SortOption) -> Unit,
    currentSortOption: SortOption,
    onDropdownExpand: () -> Unit,
    onFavoritesClick: () -> Unit,
    onViewTypeChange: (String) -> Unit,
    viewType: String,
    onThemeChange: () -> Unit,
    currentThemeMode: ThemeMode,
    onFilterClick: () -> Unit,
    onDummyListClick: () -> Unit
) {
    if (isSearchActive) {
        SearchTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onCloseSearchClick = onCloseSearchClick
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = screenTitle,
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                IconButton(onClick = onSearchIconClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.content_desc_search)
                    )
                }
                
                Box {
                    IconButton(onClick = onDropdownExpand) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort),
                            contentDescription = stringResource(R.string.content_desc_sort)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { onDropdownExpand() }
                    ) {
                        SortOption.values().forEach { sortOption ->
                            DropdownMenuItem(
                                text = { Text(stringResource(sortOption.stringRes)) },
                                onClick = { onSortOptionClick(sortOption) },
                                leadingIcon = {
                                    if (sortOption == currentSortOption) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onFavoritesClick) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.content_desc_favorites)
                    )
                }

                IconButton(onClick = {
                    onViewTypeChange(
                        if (viewType == Constants.VIEW_TYPE_GRID) Constants.VIEW_TYPE_LIST
                        else Constants.VIEW_TYPE_GRID
                    )
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (viewType == Constants.VIEW_TYPE_GRID) R.drawable.view_list_24px
                            else R.drawable.grid_view_24px
                        ),
                        contentDescription = stringResource(R.string.content_desc_switch_view)
                    )
                }

                IconButton(onClick = onThemeChange) {
                    Icon(
                        painter = painterResource(
                            id = when (currentThemeMode) {
                                ThemeMode.LIGHT -> R.drawable.dark_mode_24px
                                ThemeMode.DARK -> R.drawable.light_mode_24px
                                ThemeMode.SYSTEM -> R.drawable.contrast_24px
                            }
                        ),
                        contentDescription = stringResource(R.string.content_desc_toggle_theme)
                    )
                }

                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = stringResource(R.string.content_desc_filter)
                    )
                }

                IconButton(onClick = onDummyListClick) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = stringResource(R.string.content_desc_dummy_list)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearchClick: () -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(stringResource(R.string.label_search_movies))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.content_desc_search)
            )
        },
        trailingIcon = {
            IconButton(onClick = onCloseSearchClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_desc_close_search)
                )
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}