package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.models.DummyItem

@Composable
fun DummyDetailScreen(itemId: Int, onBackPress: () -> Unit) {
    val item = DummyItem(
        id = itemId,
        title = "Item $itemId",
        description = "This is a detailed description for item $itemId. " +
                "It contains more information about the item and is longer than the list description."
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Item Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = item.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(32.dp))
            // Add more content here to make the screen scrollable
            repeat(20) { index ->
                Text(
                    text = "Additional information $index",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}