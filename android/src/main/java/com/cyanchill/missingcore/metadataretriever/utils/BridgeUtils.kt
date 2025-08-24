package com.cyanchill.missingcore.metadataretriever.utils

import com.facebook.react.bridge.ReadableArray

object BridgeUtils {
  fun readableStringArrayToList(readableArray: ReadableArray): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until readableArray.size()) {
      list.add(readableArray.getString(i) as String)
    }
    return list
  }
}
