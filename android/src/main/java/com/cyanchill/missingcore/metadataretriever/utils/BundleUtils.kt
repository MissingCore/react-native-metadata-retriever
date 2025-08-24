package com.cyanchill.missingcore.metadataretriever.utils

import android.os.Bundle

object BundleUtils {
  fun getDouble(data: Bundle?, key: String?, defaultValue: Double): Double {
    val value = data!![key]
    return if (value is Number) value.toDouble() else defaultValue
  }

  fun getDoubleOrNull(data: Bundle?, key: String?): Double? {
    val value = data!![key]
    return if (value is Number) value.toDouble() else null
  }
}
