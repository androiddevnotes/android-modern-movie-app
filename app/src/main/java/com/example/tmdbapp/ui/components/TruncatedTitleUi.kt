import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*

@Composable
fun TruncatedTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.bodyMedium,
    color = Color.White,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
  )
}
