@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tmdbapp.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tmdbapp.R
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.*

@Composable
fun MovieDetailScreen(
  viewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val movie by viewModel.currentMovie.collectAsState()

  when {
    movie == null -> {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    }
    else -> {
      MovieDetailContent(
        movie = movie!!,
        onBackPress = onBackPress,
        onFavoriteClick = { viewModel.toggleFavorite(movie!!) },
        onDownloadClick = viewModel::downloadImage,
      )
    }
  }
}

@Composable
fun MovieDetailContent(
  movie: Movie,
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: (String?, Context) -> Unit,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize()) {
    MovieBackgroundImage(movie.posterPath)
    GradientOverlay()
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .verticalScroll(scrollState),
    ) {
      MovieDetailTopBar(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(movie.posterPath, context) },
        isFavorite = movie.isFavorite,
      )
      Spacer(modifier = Modifier.weight(1f))
      MovieDetailInfo(movie)
    }
  }
}

@Composable
fun MovieBackgroundImage(posterPath: String?) {
  AsyncImage(
    model = "https://image.tmdb.org/t/p/w500$posterPath",
    contentDescription = null,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop,
  )
}

@Composable
fun GradientOverlay() {
  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xCC000000)),
            startY = 300f,
          ),
        ),
  )
}

@Composable
fun MovieDetailTopBar(
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: () -> Unit,
  isFavorite: Boolean,
) {
  TopAppBar(
    title = { },
    navigationIcon = {
      IconButton(onClick = onBackPress) {
        Icon(
          Icons.Filled.ArrowBack,
          contentDescription = stringResource(R.string.back),
          tint = Color.White,
        )
      }
    },
    actions = {
      IconButton(onClick = onFavoriteClick) {
        Icon(
          imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
          contentDescription = stringResource(R.string.favorite),
          tint = if (isFavorite) Color.Red else Color.White,
        )
      }
      IconButton(onClick = onDownloadClick) {
        Icon(
          painter = painterResource(id = R.drawable.download_24px),
          contentDescription = stringResource(R.string.download_image),
          tint = Color.White,
        )
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White,
        actionIconContentColor = Color.White,
      ),
  )
}

@Composable
fun MovieDetailInfo(movie: Movie) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(16.dp),
  ) {
    Text(
      text = movie.title,
      style = MaterialTheme.typography.headlineLarge,
      color = Color.White,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    movie.releaseDate?.let { releaseDate ->
      Text(
        text = stringResource(R.string.release_date, releaseDate),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.7f),
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
    Text(
      text = stringResource(R.string.rating, movie.voteAverage),
      style = MaterialTheme.typography.bodyMedium,
      color = Color.White.copy(alpha = 0.7f),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.overview),
      style = MaterialTheme.typography.titleMedium,
      color = Color.White,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = movie.overview,
      style = MaterialTheme.typography.bodyMedium,
      color = Color.White.copy(alpha = 0.9f),
    )
  }
}
