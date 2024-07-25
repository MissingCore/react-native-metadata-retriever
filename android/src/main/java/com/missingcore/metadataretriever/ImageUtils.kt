package com.missingcore.metadataretriever

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

  return "data:$mimeType;base64,${Base64.encodeToString(bytes as ByteArray, Base64.DEFAULT)}"
}
