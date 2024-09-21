package com.example.tmdbapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.RepeatMode.Restart
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.ui.theme.*

@Composable
fun ShimmeringOverlay(isVisible: Boolean) {
  val transition = rememberInfiniteTransition(label = "ShimmerTransition")
  val translateAnim by transition.animateFloat(
    initialValue = -1000f,
    targetValue = 2000f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(3000, easing = LinearEasing),
        repeatMode = Restart,
      ),
    label = "ShimmerTranslate",
  )

  val shimmerColors =
    listOf(
      Color.Transparent,
      AIYellow.copy(alpha = 0.3f),
      AIPink.copy(alpha = 0.3f),
      AICyan.copy(alpha = 0.3f),
      AIGreen.copy(alpha = 0.3f),
      Color.Transparent,
    )

  val brush =
    Brush.linearGradient(
      colors = shimmerColors,
      start = Offset(0f, translateAnim),
      end = Offset(0f, translateAnim + 500f),
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
          .background(Color(0x80000000))
          .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.Lighten)
          },
    ) {
      // Scanning line
      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(
              Brush.horizontalGradient(
                listOf(AIYellow, AIPink, AICyan, AIGreen),
              ),
            ).align(Alignment.TopCenter)
            .offset(y = (translateAnim % 2000f).dp)
            .blur(radius = 10.dp),
      )
    }
  }
}
