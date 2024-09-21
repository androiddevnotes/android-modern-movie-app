@Composable
fun MovieDetailsScreen(/* ... */) {
  // ... other code
  Text(
    text = movie.title,
    style = MaterialTheme.typography.displayLarge,
    modifier = Modifier.padding(16.dp),
  )
  Text(
    text = movie.overview,
    style = MaterialTheme.typography.bodyMedium,
    modifier = Modifier.padding(16.dp),
  )
  // ... other code
}
