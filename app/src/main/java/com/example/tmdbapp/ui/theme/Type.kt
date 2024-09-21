package com.example.tmdbapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.googlefonts.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.R

val provider =
  GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
  )

val honkFont = GoogleFont("Honk")

val honkFontFamily =
  FontFamily(
    Font(googleFont = honkFont, fontProvider = provider),
  )

val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
      ),
    displayLarge =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
      ),
    headlineLarge =
      TextStyle(
        fontFamily = honkFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
      ),
  )
