package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var genres by remember { mutableStateOf(currentFilters.genres) }
    var minRating by remember { mutableStateOf(currentFilters.minRating) }
    var releaseYear by remember { mutableStateOf(currentFilters.releaseYear) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Filter Movies", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            
            
            
            
            
            

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(onClick = {
                    onApply(FilterOptions(genres, minRating, releaseYear))
                    onDismiss()
                }) {
                    Text("Apply")
                }
            }
        }
    }
}