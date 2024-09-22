package com.example.tmdbapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.RepeatMode.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import com.example.tmdbapp.ui.theme.*

@Composable
fun AiScanningIndicatorUi() {
  val scanningTexts =
    listOf(
      "Initializing scan",
      "Scanning poster",
      "Analyzing title",
      "Processing overview",
      "Evaluating rating",
      "Generating insights",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "ScanningTransition")
  val textIndex by infiniteTransition.animateValue(
    initialValue = 0,
    targetValue = scanningTexts.size,
    typeConverter = Int.VectorConverter,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 1000, easing = LinearEasing),
        repeatMode = Restart,
      ),
    label = "ScanningTextIndex",
  )

  val dotCount by infiniteTransition.animateValue(
    initialValue = 0,
    targetValue = 3,
    typeConverter = Int.VectorConverter,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 600, easing = LinearEasing),
        repeatMode = Reverse,
      ),
    label = "DotAnimation",
  )

  val colors = listOf(AIYellow, AIPink, AICyan, AIGreen)
  val currentColorIndex by infiniteTransition.animateValue(
    initialValue = 0,
    targetValue = colors.size,
    typeConverter = Int.VectorConverter,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 1000, easing = LinearEasing),
        repeatMode = Restart,
      ),
    label = "ColorAnimation",
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Text(
      text = scanningTexts[textIndex % scanningTexts.size],
      style = MaterialTheme.typography.titleMedium,
      color = colors[currentColorIndex % colors.size],
      fontWeight = FontWeight.Bold,
    )
    Text(
      text = ".".repeat(dotCount),
      style = MaterialTheme.typography.titleMedium,
      color = colors[currentColorIndex % colors.size],
      fontWeight = FontWeight.Bold,
    )
  }
}
