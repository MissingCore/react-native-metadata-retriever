package com.cyanchill.missingcore.metadataretriever

import android.util.Base64
import androidx.media3.common.MimeTypes
import java.io.ByteArrayOutputStream
import java.net.URLConnection


/**
 * Returns a base64 image string from a `ByteArray`.
 */
fun getBase64Image(bytes: ByteArray?): String? {
  if (bytes == null) return null

  // Determine mimetype from bytes.
  val mimeType = URLConnection.guessContentTypeFromStream(bytes.inputStream())?.let {
    MimeTypes.normalizeMimeType(it)
  }
  // Ensure the mimeType we get is defined and is for an image.
  if (!MimeTypes.isImage(mimeType)) return null

  // Use simplier & faster method for converting the byte array to a string based on its size (<5MB).
  if (bytes.size < 5 * 1024 * 1024) {
    return "data:$mimeType;base64,${Base64.encodeToString(bytes as ByteArray, Base64.DEFAULT)}"
  }

  // Process large byte array (>=5MB) more efficiently to prevent `OutOfMemoryError`.
  val outputStream = ByteArrayOutputStream()
  bytes.asSequence()
    .chunked(3 * 1024 * 1024) // 4 characters usually convert to 3 bytes; so we should chunk in multiples of 3.
    .forEach { chunk ->
      val encodedChunk = Base64.encode(chunk.toByteArray(), Base64.NO_WRAP)
      outputStream.write(encodedChunk)
    }

  return "data:$mimeType;base64,${outputStream.toString()}"
}

/**
 * Returns the year from ISO 8601 format (ie: `YYYY-MM-DD`).
 */
fun parseYear(_dateString: Any?): Int? {
  if (_dateString == null) return null
  val dateString = _dateString.toString()
  return dateString.substring(0, 4).toIntOrNull()
}
