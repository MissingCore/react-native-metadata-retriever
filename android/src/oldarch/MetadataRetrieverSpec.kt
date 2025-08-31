package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise

import android.os.Bundle

abstract class MetadataRetrieverSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun getMetadata(uri: String, options: ReadableArray, promise: Promise)

  abstract fun getArtwork(uri: String, promise: Promise)

  abstract fun updateConfigs(options: Bundle)
}
