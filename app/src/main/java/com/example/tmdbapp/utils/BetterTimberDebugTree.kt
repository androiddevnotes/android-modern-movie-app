package com.example.tmdbapp.utils

import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.regex.Pattern

class BetterTimberDebugTree(
  private val globalTag: String = "GTAG",
) : Timber.DebugTree() {
  private val json = Json { prettyPrint = true }
  private val jsonPattern: Pattern =
    Pattern.compile("(\\{(?:[^{}]|(?:\\{(?:[^{}]|(?:\\{[^{}]*\\}))*\\}))*\\})")

  override fun log(
    priority: Int,
    tag: String?,
    message: String,
    t: Throwable?,
  ) {
    findLogCallStackTraceElement()?.let { element ->
      val lineNumberInfo = "(${element.fileName}:${element.lineNumber})"
      val formattedMessage = formatJsonIfNeeded(message)
      val updatedMessage = "$lineNumberInfo: $formattedMessage"
      super.log(priority, "$globalTag-$tag", updatedMessage, t)
    } ?: run {
      super.log(priority, "$globalTag-$tag", message, t)
    }
  }

  override fun createStackElementTag(element: StackTraceElement): String? = element.fileName

  private fun findLogCallStackTraceElement(): StackTraceElement? {
    val stackTrace = Throwable().stackTrace
    var foundDebugTree = false
    return stackTrace.firstOrNull { element ->
      if (element.className.contains("BetterTimberDebugTree")) {
        foundDebugTree = true
        false
      } else {
        foundDebugTree && !element.className.contains("Timber")
      }
    }
  }

  private fun formatJsonIfNeeded(message: String): String {
    val matcher = jsonPattern.matcher(message)
    val buffer = StringBuffer()
    while (matcher.find()) {
      try {
        val jsonString = matcher.group()
        val parsedJson = json.parseToJsonElement(jsonString)
        val formattedJson =
          json.encodeToString(
            kotlinx.serialization.json.JsonElement
              .serializer(),
            parsedJson,
          )
        matcher.appendReplacement(buffer, formattedJson.replace("$", "\\$"))
      } catch (_: Exception) {
        // If parsing fails, leave the original JSON string unchanged
      }
    }
    matcher.appendTail(buffer)
    return buffer.toString()
  }
}
