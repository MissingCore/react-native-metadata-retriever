package com.cyanchill.missingcore.metadataretriever.utils

object MapUtils {
  fun getBoolean(map: HashMap<String, Any?>, key: String): Boolean? {
    return map[key] as? Boolean
  }

  fun getDouble(map: HashMap<String, Any?>, key: String): Double? {
    return map[key] as? Double
  }

  fun getInt(map: HashMap<String, Any?>, key: String): Int? {
    return map[key] as? Int
  }

  fun getString(map: HashMap<String, Any?>, key: String): String? {
    return map[key] as? String
  }
}
