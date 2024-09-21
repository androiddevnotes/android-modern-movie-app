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
        repeatMode = RepeatMode.Restart,
      ),
    label = "ShimmerTranslate",
  )

  val shimmerColors =
    listOf(
      Color(0x00FFFFFF),
      Color(0xFF00FFFF).copy(alpha = 0.2f), // Cyan
      Color(0xFF1E90FF).copy(alpha = 0.2f), // Dodger Blue
      Color(0xFF00FFFF).copy(alpha = 0.2f), // Cyan
      Color(0x00FFFFFF),
    )

  val brush =
    Brush.linearGradient(
      colors = shimmerColors,
      start = Offset(translateAnim, 0f),
      end = Offset(translateAnim + 1000f, 2000f),
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
          .background(Color(0x80000000)) // Semi-transparent black background
          .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.Screen)
          },
    ) {
      // Angelic scanning line
      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0x00FFFFFF),
                  Color(0xFFFFD700), // Gold
                  Color(0xFFFFFAFA), // Snow white
                  Color(0xFF87CEFA), // Light sky blue
                  Color(0x00FFFFFF),
                ),
              ),
            ).align(Alignment.TopCenter)
            .offset(y = (translateAnim % 2000f).dp)
            .blur(radius = 8.dp),
      )
    }
  }
}
