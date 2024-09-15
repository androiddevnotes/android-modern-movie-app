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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale

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
    onFilterClick: () -> Unit
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
                    Image(
                        painter = painterResource(id = R.drawable.cool_shape_search),
                        contentDescription = stringResource(R.string.content_desc_search),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Box {
                    IconButton(onClick = onDropdownExpand) {
                        Image(
                            painter = painterResource(id = R.drawable.cool_shape_sort),
                            contentDescription = stringResource(R.string.content_desc_sort),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
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
                    Image(
                        painter = painterResource(id = R.drawable.cool_shape_fav),
                        contentDescription = stringResource(R.string.content_desc_favorites),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = {
                    onViewTypeChange(
                        if (viewType == Constants.VIEW_TYPE_GRID) Constants.VIEW_TYPE_LIST
                        else Constants.VIEW_TYPE_GRID
                    )
                }) {
                    Image(
                        painter = painterResource(
                            id = if (viewType == Constants.VIEW_TYPE_GRID) R.drawable.cool_shape_list
                            else R.drawable.cool_shape_grid
                        ),
                        contentDescription = stringResource(R.string.content_desc_switch_view),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onThemeChange) {
                    Image(
                        painter = painterResource(
                            id = when (currentThemeMode) {
                                ThemeMode.LIGHT -> R.drawable.cool_shape_night
                                ThemeMode.DARK -> R.drawable.cool_shape_light
                                ThemeMode.SYSTEM -> R.drawable.cool_shape_theme_system
                            }
                        ),
                        contentDescription = stringResource(R.string.content_desc_toggle_theme),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onFilterClick) {
                    Image(
                        painter = painterResource(id = R.drawable.cool_shape_filter),
                        contentDescription = stringResource(R.string.content_desc_filter),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
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