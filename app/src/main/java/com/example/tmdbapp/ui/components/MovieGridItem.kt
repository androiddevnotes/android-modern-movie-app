import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.utils.Constants

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieGridItem(
    movie: Movie,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isFavorite: Boolean,
) {
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
        Box {
            SubcomposeAsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(Constants.BASE_IMAGE_URL + movie.posterPath)
                        .crossfade(true)
                        .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }

                    is AsyncImagePainter.State.Error -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Text(
                                text = movie.title.take(1),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }

                    else -> {
                        this@SubcomposeAsyncImage.painter.let {
                            Image(painter = it, contentDescription = movie.title)
                        }
                    }
                }
            }
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
                maxLines = 2,
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
