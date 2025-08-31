package com.cyanchill.missingcore.metadataretriever.utils

import android.os.Bundle

object BundleUtils {
  fun putDoubleIfExists(key: String, data: Bundle, store: Bundle) {
    if (data.containsKey(key)) store.putDouble(key, data.getDouble(key))
  }
}
