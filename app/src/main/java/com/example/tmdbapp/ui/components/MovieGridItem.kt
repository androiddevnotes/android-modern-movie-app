import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.*
import coil.request.*
import com.example.tmdbapp.R
import com.example.tmdbapp.models.*
import com.example.tmdbapp.utils.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieGridItem(
  movie: Movie,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  isFavorite: Boolean,
) {
  val context = LocalContext.current
  val imageRequest =
    remember(movie.posterPath) {
      ImageRequest
        .Builder(context)
        .data(Constants.BASE_IMAGE_URL + movie.posterPath)
        .crossfade(true)
        .build()
    }

  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .aspectRatio(2f / 3f)
        .combinedClickable(
          onClick = onClick,
          onLongClick = onLongClick,
        ).then(
          if (isFavorite) {
            Modifier.border(
              width = 2.dp,
              color = MaterialTheme.colorScheme.primary,
              shape = RoundedCornerShape(4.dp),
            )
          } else {
            Modifier
          },
        ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    shape = RoundedCornerShape(4.dp),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      AsyncImage(
        model = imageRequest, // Updated from movie.posterUrl to imageRequest
        contentDescription = movie.title,
        placeholder = painterResource(R.drawable.cool_shape_placeholder),
        error = painterResource(R.drawable.cool_shape_grid),
        contentScale = ContentScale.Crop,
        modifier =
          Modifier
            .size(128.dp)
            .background(MaterialTheme.colorScheme.surface),
      )
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                startY = 300f,
              ),
            ),
      )
      Text(
        text = movie.title,
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        maxLines = Constants.MAX_LINES_TITLE,
        overflow = TextOverflow.Ellipsis,
      )
      if (isFavorite) {
        Box(
          modifier =
            Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .height(4.dp)
              .background(MaterialTheme.colorScheme.primary),
        )
      }
    }
  }
}
