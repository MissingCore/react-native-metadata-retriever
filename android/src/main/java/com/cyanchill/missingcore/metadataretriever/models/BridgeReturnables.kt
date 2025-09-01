package com.cyanchill.missingcore.metadataretriever.models.BridgeReturnables

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap

fun ErrorObject(name: String, message: String): ReadableMap {
  val value = Arguments.createMap()
  value.putString("name", name)
  value.putString("message", message)
  return value
}

fun ResultObject(name: String, data: ReadableMap): ReadableMap {
  val value = Arguments.createMap()
  value.putString("uri", name)
  value.putMap("data", data)
  return value
}
