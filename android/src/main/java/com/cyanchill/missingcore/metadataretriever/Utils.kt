package com.cyanchill.missingcore.metadataretriever

import android.util.Base64
import androidx.media3.common.MimeTypes
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

  // Set hard-cap on the amount of bytes we'll convert to base64 to 3.75MB. This is because when
  // converting a byte array to a base64 string, we see a 33-37% increase in the size (bringing up
  // to a max return size of ~5MB).
  if (bytes.size > 3.75 * 1024 * 1024) return null
  return "data:$mimeType;base64,${Base64.encodeToString(bytes, Base64.DEFAULT)}"
}

/**
 * Returns the year from ISO 8601 format (ie: `YYYY-MM-DD`).
 */
fun parseYear(_dateString: Any?): Int? {
  if (_dateString == null) return null
  val dateString = _dateString.toString()
  return dateString.substring(0, 4).toIntOrNull()
}
