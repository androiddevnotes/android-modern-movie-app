package com.example.tmdbapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.utils.Constants

@Composable
fun MovieItem(
    movie: Movie,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit,
    isListView: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isListView) 150.dp else Constants.MOVIE_ITEM_HEIGHT),
        elevation = CardDefaults.cardElevation(defaultElevation = Constants.CARD_ELEVATION),
        shape = RoundedCornerShape(Constants.CARD_CORNER_RADIUS)
    ) {
        if (isListView) {
            Row(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "${Constants.BASE_IMAGE_URL}${movie.posterPath}",
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(
                                topStart = Constants.CARD_CORNER_RADIUS,
                                bottomStart = Constants.CARD_CORNER_RADIUS
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (movie.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "${Constants.BASE_IMAGE_URL}${movie.posterPath}",
                    contentDescription = movie.title,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xE6000000)),
                                startY = 300f,
                                endY = 900f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0x80000000))
                ) {
                    Icon(
                        imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (movie.isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}