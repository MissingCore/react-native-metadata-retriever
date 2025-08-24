package com.cyanchill.missingcore.metadataretriever.utils

import androidx.media3.common.Format

object NormalizationUtils {
  /**
   * Return `null` if we see `Format.NO_VALUE` (-1).
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format#NO_VALUE()">Link</a>
   */
  fun fixNoValue(intVal: Int?): Int? = when (intVal) {
    null, Format.NO_VALUE -> null
    else -> intVal
  }

  /** Returns a string that safely handles special characters such as "%", "?", and "#". */
  fun getSafeUri(uri: String): String {
    // It's important to replace the "%" first as if we put it later on, it'll
    // break the decoding for "?" & "#".
    return uri.replace("%", "%25").replace("?", "%3F").replace("#", "%23")
  }

  /** Returns the year from ISO 8601 format (ie: `YYYY-MM-DD`). */
  fun parseYear(dateTime: Any?): Int? {
    if (dateTime == null) return null
    val dateTimeString = dateTime.toString() // We expect `dateTime` to be a `String` or `Int`.
    if (dateTimeString.length < 4) return null
    return dateTimeString.substring(0, 4).toIntOrNull()
  }
}
