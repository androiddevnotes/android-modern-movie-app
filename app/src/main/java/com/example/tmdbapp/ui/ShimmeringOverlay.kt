package com.example.tmdbapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
fun ShimmeringOverlay(isVisible: Boolean) {
  val transition = rememberInfiniteTransition(label = "ShimmerTransition")
  val progress by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(3000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse,
      ),
    label = "ShimmerProgress",
  )

  val shimmerColors =
    listOf(
      Color(0x00FFFFFF),
      Color(0xFF00FFFF).copy(alpha = 0.2f),
      Color(0xFF1E90FF).copy(alpha = 0.2f),
      Color(0xFF00FFFF).copy(alpha = 0.2f),
      Color(0x00FFFFFF),
    )

  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    BoxWithConstraints(
      modifier =
        Modifier
          .fillMaxSize()
          .background(Color(0x80000000)),
    ) {
      val boxHeight = constraints.maxHeight.toFloat()
      val boxWidth = constraints.maxWidth.toFloat()

      val brush =
        Brush.linearGradient(
          colors = shimmerColors,
          start = Offset(-1000f + progress * (boxWidth + 2000f), 0f),
          end = Offset(progress * (boxWidth + 2000f), boxHeight),
          tileMode = TileMode.Clamp,
        )

      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .drawWithContent {
              drawContent()
              drawRect(brush = brush, blendMode = BlendMode.Screen)
            },
      )

      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0x00FFFFFF),
                  Color(0xFFFFD700),
                  Color(0xFFFFFAFA),
                  Color(0xFF87CEFA),
                  Color(0x00FFFFFF),
                ),
              ),
            ).align(Alignment.TopCenter)
            .offset(y = (progress * boxHeight).dp)
            .blur(radius = 8.dp),
      )
    }
  }
}
