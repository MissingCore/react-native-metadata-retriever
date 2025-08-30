package com.cyanchill.missingcore.metadataretriever.utils

/** Dynamically get type-safe results from a map at a given key. */
object MapUtils {
  fun getBoolean(map: HashMap<String, Any?>, key: String) = map[key] as? Boolean

  fun getDouble(map: HashMap<String, Any?>, key: String) = map[key] as? Double

  fun getInt(map: HashMap<String, Any?>, key: String) = map[key] as? Int

  fun getString(map: HashMap<String, Any?>, key: String) = map[key] as? String
}
