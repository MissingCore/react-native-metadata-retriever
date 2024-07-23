package com.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise

abstract class MetadataRetrieverSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  abstract fun getMetadata(uri: String, options: ReadableArray, promise: Promise)
}
