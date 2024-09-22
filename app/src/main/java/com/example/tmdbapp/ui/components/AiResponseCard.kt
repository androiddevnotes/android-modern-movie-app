package com.example.tmdbapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.ui.theme.*

@Composable
fun AiResponseCard(
  response: String,
  visible: Boolean = true,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")
  val animatedDegrees by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(10000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
      ),
    label = "BorderRotation",
  )

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
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        ),
    ) {
      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .drawBehind {
              val borderWidth = 4.dp.toPx()
              val gradientBrush =
                Brush.sweepGradient(
                  colors = listOf(AIYellow, AIPink, AICyan, AIGreen),
                  center = Offset(size.width / 2, size.height / 2),
                )
              rotate(animatedDegrees) {
                drawRoundRect(
                  brush = gradientBrush,
                  size = size,
                  cornerRadius = CornerRadius(16.dp.toPx()),
                  style = Stroke(width = borderWidth),
                )
              }
            }.padding(4.dp),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "AI Response:",
            style = MaterialTheme.typography.titleMedium,
            color = AICyan,
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
}
