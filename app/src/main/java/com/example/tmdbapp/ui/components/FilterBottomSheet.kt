import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.R
import com.example.tmdbapp.viewmodel.FilterOptions
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: FilterOptions,
    onDismiss: () -> Unit,
    onApply: (FilterOptions) -> Unit
) {
    var selectedGenres by remember { mutableStateOf(currentFilters.genres.toSet()) }
    var selectedYear by remember { mutableStateOf(currentFilters.releaseYear) }
    var minRating by remember { mutableStateOf(currentFilters.minRating ?: 0f) }
    var isYearMenuExpanded by remember { mutableStateOf(false) }

    val currentYear = LocalDate.now().year
    val yearRange = (1900..currentYear).reversed()

    val genres = listOf(
         28 to "Action",
         12 to "Adventure",
         16 to "Animation",
         35 to "Comedy",
         80 to "Crime",
         18 to "Drama",
         14 to "Fantasy",
         27 to "Horror",
         10749 to "Romance",
         878 to "Science Fiction"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.filter_movies), style = MaterialTheme.typography.titleLarge)
            
            Text(stringResource(R.string.genres), style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres) { (id, name) ->
                    FilterChip(
                        selected = selectedGenres.contains(id),
                        onClick = {
                            selectedGenres = if (selectedGenres.contains(id)) {
                                selectedGenres - id
                            } else {
                                selectedGenres + id
                            }
                        },
                        label = { Text(name) }
                    )
                }
            }

            Text(stringResource(R.string.release_year), style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = isYearMenuExpanded,
                onExpandedChange = { isYearMenuExpanded = !isYearMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedYear?.toString() ?: "",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isYearMenuExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isYearMenuExpanded,
                    onDismissRequest = { isYearMenuExpanded = false }
                ) {
                    yearRange.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                selectedYear = year
                                isYearMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Text(stringResource(R.string.minimum_rating), style = MaterialTheme.typography.titleMedium)
            Column {
                Slider(
                    value = minRating,
                    onValueChange = { minRating = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
                Text(
                    text = stringResource(R.string.rating_value, minRating),
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onApply(
                        FilterOptions(
                            genres = selectedGenres.toList(),
                            releaseYear = selectedYear,
                            minRating = if (minRating > 0f) minRating else null
                        )
                    )
                    onDismiss()
                }) {
                    Text(stringResource(R.string.apply))
                }
            }
        }
    }
}