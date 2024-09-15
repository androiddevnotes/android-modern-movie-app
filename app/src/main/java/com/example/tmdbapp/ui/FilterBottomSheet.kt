import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.viewmodel.FilterOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: FilterOptions,
    onDismiss: () -> Unit,
    onApply: (FilterOptions) -> Unit
) {
    var selectedGenres by remember { mutableStateOf(currentFilters.genres.toSet()) }
    var releaseYear by remember { mutableStateOf(currentFilters.releaseYear?.toString() ?: "") }
    var minRating by remember { mutableStateOf(currentFilters.minRating?.toString() ?: "") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filter Movies", style = MaterialTheme.typography.titleLarge)
            
            Text("Genres", style = MaterialTheme.typography.titleMedium)
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
            OutlinedTextField(
                value = releaseYear,
                onValueChange = { releaseYear = it },
                label = { Text("Release Year") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = minRating,
                onValueChange = { minRating = it },
                label = { Text("Minimum Rating") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onApply(
                        FilterOptions(
                            genres = selectedGenres.toList(),
                            releaseYear = releaseYear.toIntOrNull(),
                            minRating = minRating.toFloatOrNull()
                        )
                    )
                    onDismiss()
                }) {
                    Text("Apply")
                }
            }
        }
    }
}