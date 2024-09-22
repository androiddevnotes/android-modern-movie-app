package com.example.tmdbapp.viewmodel

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.provider.*
import android.widget.*
import androidx.lifecycle.*
import coil.*
import coil.request.*
import com.example.tmdbapp.*
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*
import java.io.*

fun ItemViewModel.downloadImage(
  posterPath: String?,
  context: Context,
) {
  viewModelScope.launch {
    if (posterPath == null) {
      Toast.makeText(context, context.getString(R.string.error_no_image), Toast.LENGTH_SHORT).show()
      return@launch
    }

    val imageUrl = "${Constants.BASE_IMAGE_URL}$posterPath"

    try {
      val bitmap =
        withContext(Dispatchers.IO) {
          val loader = ImageLoader(context)
          val request =
            ImageRequest
              .Builder(context)
              .data(imageUrl)
              .allowHardware(false)
              .build()

          val result = (loader.execute(request) as SuccessResult).drawable
          (result as BitmapDrawable).bitmap
        }

      val filename = "TMDB_${System.currentTimeMillis()}.jpg"
      val contentValues =
        ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
          }
        }

      val resolver = context.contentResolver
      val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

      uri?.let {
        withContext(Dispatchers.IO) {
          resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
          }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          contentValues.clear()
          contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
          resolver.update(it, contentValues, null, null)
        }

        Toast
          .makeText(context, context.getString(R.string.success_image_saved), Toast.LENGTH_SHORT)
          .show()
      } ?: run {
        Toast
          .makeText(
            context,
            context.getString(R.string.error_failed_to_save),
            Toast.LENGTH_SHORT,
          ).show()
      }
    } catch (e: IOException) {
      Toast
        .makeText(
          context,
          "${context.getString(R.string.error_download_failed)}: ${e.localizedMessage}",
          Toast.LENGTH_SHORT,
        ).show()
    }
  }
}
