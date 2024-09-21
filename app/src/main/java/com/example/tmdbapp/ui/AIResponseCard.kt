package com.example.tmdbapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.ui.theme.*

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
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        ),
      border =
        BorderStroke(
          1.dp,
          Brush.linearGradient(listOf(AIYellow, AIPink, AICyan, AIGreen)),
        ),
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
