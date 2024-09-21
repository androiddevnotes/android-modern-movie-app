package com.example.tmdbapp.ui

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.tmdbapp.R
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.*
import kotlin.random.Random

@Composable
fun MovieDetailScreen(
  viewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val movie by viewModel.currentMovie.collectAsState()
  val aiResponse by viewModel.aiResponse.collectAsState()
  val aiResponseState by viewModel.aiResponseState.collectAsState()

  // Clear AI response when leaving the screen
  DisposableEffect(Unit) {
    onDispose {
      viewModel.clearAIResponse()
    }
  }

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
        onAskAIClick = { viewModel.askAIAboutMovie(movie!!) },
        aiResponse = aiResponse,
        aiResponseState = aiResponseState,
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
  onAskAIClick: () -> Unit,
  aiResponse: String?,
  aiResponseState: AIResponseState,
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
          .verticalScroll(scrollState)
          .padding(bottom = 80.dp), // Add padding to account for bottom nav bar
    ) {
      MovieDetailTopBar(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(movie.posterPath, context) },
        isFavorite = movie.isFavorite,
      )
      Spacer(modifier = Modifier.weight(1f))
      MovieDetailInfo(movie)

      // Add AI Button and Response
      Spacer(modifier = Modifier.height(16.dp))
      when (aiResponseState) {
        AIResponseState.Idle -> {
          GrainyGradientButton(
            onClick = onAskAIClick,
            text = stringResource(R.string.ask_ai_about_movie),
          )
        }
        AIResponseState.Loading -> {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
            )
          }
        }
        is AIResponseState.Error -> {
          Text(
            text = (aiResponseState as AIResponseState.Error).message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp),
          )
          Spacer(modifier = Modifier.height(8.dp))
          GrainyGradientButton(
            onClick = onAskAIClick,
            text = stringResource(R.string.retry_ai_request),
          )
        }
        AIResponseState.Success -> {
          aiResponse?.let { response ->
            AIResponseCard(response = response)
          }
        }
      }
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
          tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
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

@Composable
fun GrainyGradientButton(
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
) {
  val gradientBrush =
    Brush.linearGradient(
      colors =
        listOf(
          Color(0xFF607D8B), // Blue Grey 500
          Color(0xFF455A64), // Blue Grey 700
          Color(0xFF37474F), // Blue Grey 800
        ),
      start = Offset(0f, 0f),
      end = Offset(100f, 100f),
      tileMode = TileMode.Clamp,
    )

  Button(
    onClick = onClick,
    modifier =
      modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .height(48.dp)
        .clip(MaterialTheme.shapes.small)
        .drawBehind {
          drawRect(gradientBrush)
          // Reduced grainy effect
          repeat(500) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            drawCircle(
              color = Color.White.copy(alpha = 0.05f),
              radius = 0.5f,
              center = Offset(x, y),
            )
          }
        },
    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
    elevation =
      ButtonDefaults.buttonElevation(
        defaultElevation = 4.dp,
        pressedElevation = 8.dp,
      ),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
      color = Color.White,
    )
  }
}

@Composable
fun AIResponseCard(response: String) {
  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
    shape = MaterialTheme.shapes.medium,
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "AI Response:",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = response,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Justify,
      )
    }
  }
}
