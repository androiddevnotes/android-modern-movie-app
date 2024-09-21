package com.example.tmdbapp.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.utils.MovieError
import com.example.tmdbapp.viewmodel.*

@Composable
fun MovieDetailScreen(
  viewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val movieState by viewModel.movieDetailState.collectAsState()
  val aiResponse by viewModel.aiResponse.collectAsState()
  val aiResponseState by viewModel.aiResponseState.collectAsState()

  // Clear AI response when leaving the screen
  DisposableEffect(Unit) {
    onDispose {
      viewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (movieState) {
      is MovieDetailState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }
      is MovieDetailState.Success -> {
        val movie = (movieState as MovieDetailState.Success).movie
        MovieDetailContent(
          movie = movie,
          onBackPress = onBackPress,
          onFavoriteClick = { viewModel.toggleFavorite(movie) },
          onDownloadClick = viewModel::downloadImage,
          onAskAIClick = { viewModel.askAIAboutMovie(movie) },
          aiResponse = aiResponse,
          aiResponseState = aiResponseState,
        )
      }
      is MovieDetailState.Error -> {
        ErrorContent(
          error = (movieState as MovieDetailState.Error).error,
          onRetry = { viewModel.retryFetchMovieDetails() },
          onBackPress = onBackPress,
        )
      }
    }

    // Shimmering overlay
    ShimmeringOverlay(
      isVisible = aiResponseState == AIResponseState.Loading,
    )

    // AI thinking indicator
    if (aiResponseState == AIResponseState.Loading) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        Text(
          text = "AI is thinking...",
          style = MaterialTheme.typography.titleMedium,
          color = Color.White,
          fontWeight = FontWeight.Bold,
        )
      }
    }
  }
}

@Composable
fun ShimmeringOverlay(isVisible: Boolean) {
  val transition = rememberInfiniteTransition(label = "ShimmerTransition")
  val translateAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
      ),
    label = "ShimmerTranslate",
  )

  val alphaAnim by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.5f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(1000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse,
      ),
    label = "ShimmerAlpha",
  )

  val shimmerColors =
    listOf(
      Color(0x00FFFFFF),
      Color(0x40FFFFFF),
      Color(0x80FFFFFF),
      Color(0x40FFFFFF),
      Color(0x00FFFFFF),
    )

  val brush =
    Brush.linearGradient(
      colors = shimmerColors,
      start = Offset(translateAnim - 1000f, translateAnim - 1000f),
      end = Offset(translateAnim, translateAnim),
      tileMode = TileMode.Clamp,
    )

  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .background(Color(0x40000000))
          .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.Lighten)
          }.alpha(alphaAnim),
    )
  }
}

@Composable
fun ShimmeringBox(
  modifier: Modifier = Modifier,
  isShimmering: Boolean = true,
  content: @Composable () -> Unit,
) {
  val shimmerColors =
    listOf(
      Color.White.copy(alpha = 0.3f),
      Color.White.copy(alpha = 0.5f),
      Color.White.copy(alpha = 0.3f),
    )

  val transition = rememberInfiniteTransition(label = "ShimmerTransition")
  val translateAnim =
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 1000f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "ShimmerAnimation",
    )

  val brush =
    if (isShimmering) {
      Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value),
      )
    } else {
      Brush.linearGradient(
        colors = listOf(Color.Transparent, Color.Transparent),
        start = Offset.Zero,
        end = Offset.Zero,
      )
    }

  Box(
    modifier =
      modifier
        .background(brush),
  ) {
    content()
  }
}

@Composable
fun ErrorContent(
  error: MovieError,
  onRetry: () -> Unit,
  onBackPress: () -> Unit,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(error.messageResId),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onRetry) {
      Text(stringResource(R.string.retry))
    }
    Spacer(modifier = Modifier.height(8.dp))
    TextButton(onClick = onBackPress) {
      Text(stringResource(R.string.back))
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
          .padding(bottom = 80.dp),
    ) {
      MovieDetailTopBar(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(movie.posterPath, context) },
        onAskAIClick = onAskAIClick,
        isFavorite = movie.isFavorite,
      )
      Spacer(modifier = Modifier.weight(1f))
      MovieDetailInfo(movie)

      // AI Response
      Spacer(modifier = Modifier.height(16.dp))
      when (aiResponseState) {
        AIResponseState.Loading -> {
          Box(
            modifier =
              Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        is AIResponseState.Error -> {
          Text(
            text = aiResponseState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp),
          )
        }
        AIResponseState.Success -> {
          aiResponse?.let { response ->
            AIResponseCard(response = response)
          }
        }
        else -> { /* Do nothing for Idle state */ }
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
  onAskAIClick: () -> Unit,
  isFavorite: Boolean,
) {
  TopAppBar(
    title = { },
    navigationIcon = {
      IconButton(onClick = onBackPress) {
        Icon(
          Filled.ArrowBack,
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
        Image(
          painter = painterResource(id = R.drawable.my_shape_poly12),
          contentDescription = stringResource(R.string.download_image),
          modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
        )
      }
      IconButton(onClick = onAskAIClick) {
        Image(
          painter = painterResource(id = R.drawable.cool_shape_ai),
          contentDescription = stringResource(R.string.ask_ai_about_movie),
          modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
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
fun AIResponseCard(response: String) {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(response) {
    visible = true
  }

  AnimatedVisibility(
    visible = visible,
    enter =
      fadeIn() +
        expandVertically(
          expandFrom = Alignment.Top,
          animationSpec = tween(durationMillis = 300, easing = EaseOutCubic),
        ),
  ) {
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
}
