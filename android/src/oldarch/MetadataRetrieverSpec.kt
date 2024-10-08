package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise

abstract class MetadataRetrieverSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun getTypedExportedConstants(): Map<String, Any?>

  override fun getConstants(): Map<String, Any?> {
    return getTypedExportedConstants()
  }

  abstract fun getMetadata(uri: String, options: ReadableArray, promise: Promise)

  abstract fun getArtwork(uri: String, promise: Promise)
}
