package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.Promise

abstract class MetadataRetrieverSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun getBulkMetadata(uris: ReadableArray, options: ReadableArray, promise: Promise)

  abstract fun getArtwork(uri: String, promise: Promise)
  abstract fun saveArtwork(uri: String, options: ReadableMap, promise: Promise)

  abstract fun updateConfigs(options: ReadableMap, promise: Promise)
}
