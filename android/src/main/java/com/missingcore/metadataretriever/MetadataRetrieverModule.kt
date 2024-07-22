package com.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.ListenableFuture


class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {

  private val context = reactContext

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  override fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    // Validate options

    // FIXME: I guess we need to return a "readable map"?


    val mediaItem = MediaItem.fromUri(uri)
    var trackGroupArray: TrackGroupArray? = null
    try {
      trackGroupArray = MetadataRetriever.retrieveMetadata(context, mediaItem).get()
    } catch (err: Throwable) {
      promise.reject("Metadata Retrieval Error", err)
    }

    if (trackGroupArray == null) {
      // FIXME: Maybe should return the object containing what we'll return with all `null`s.
      promise.reject("No data found.")
      return
    }

    val trackGroup = trackGroupArray.get(0)
    val trackFormat = trackGroup.getFormat(0)
    val metadata = trackFormat.metadata
    if (metadata == null) {
      promise.reject("No metadata found.")
      return
    }

    val mediaMetadataBuilder = MediaMetadata.Builder()
    for (idx in 0.rangeTo(metadata.length() - 1)) {
      metadata.get(idx).populateMediaMetadata(mediaMetadataBuilder)
    }
    val mediaMetadata = mediaMetadataBuilder.build()

    promise.resolve(mediaMetadata.albumTitle)
  }


  companion object {
    const val NAME = "MetadataRetriever"
  }
}
