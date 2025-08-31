package com.cyanchill.missingcore.metadataretriever.utils

import android.os.Bundle

object BundleUtils {
  fun getDoubleOrNull(key: String, data: Bundle): Double? {
    return data[key] as? Double
  }

  fun putDoubleIfExists(key: String, data: Bundle, store: Bundle) {
    if (data.containsKey(key)) {
      val value = getDoubleOrNull(key, data)
      if (value != null) store.putDouble(key, value)
      else store.remove(key)
    }
  }
}
