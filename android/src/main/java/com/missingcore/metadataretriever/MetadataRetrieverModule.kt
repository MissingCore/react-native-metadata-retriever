package com.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.exoplayer.MetadataRetriever
import java.util.concurrent.ExecutionException

class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    // Populate return object with default values based on input.
    val metadataMap = Arguments.createMap()
    for (i in 0 until options.size()) {
      metadataMap.putNull(options.getString(i))
    }

    try {
      // Get static metadata of media from its uri.
      // See https://developer.android.com/media/media3/exoplayer/retrieving-metadata#kotlin
      val mediaItem = MediaItem.fromUri(uri)
      val trackGroupArray = MetadataRetriever.retrieveMetadata(context, mediaItem).get()

      if (trackGroupArray == null) throw TrackGroupArrayException()

      // Start unwrapping the containers returned by `MetadataRetriever.retrieveMetadata` to get
      // access to the metadata.
      val metadataList = mutableListOf<Metadata>()
      for (i in 0 until trackGroupArray.length) {
        val trackGroup = trackGroupArray[i]
        // Only look at the track group containing audio.
        if (trackGroup.type != C.TRACK_TYPE_AUDIO) continue
        for (j in 0 until trackGroup.length) {
          // There's some other data in the `Format` returned by `trackGroup.getFormat(i)` that we
          // may interest us in the future.
          val metadata = trackGroup.getFormat(j).metadata
          if (metadata == null) continue
          metadataList.add(metadata)
        }
      }

      // Format the metadata nicely.
      val mediaMetadata = MediaMetadata.Builder()
        .populateFromMetadata(metadataList)
        .build()

      // Populate return object with the metadata we found.
      for (i in 0 until options.size()) {
        val field = options.getString(i)
        val fieldData = readMediaMetadataField(mediaMetadata, field)
        // Use scope functions to help determine output.
        // See https://kotlinlang.org/docs/scope-functions.html
        when (field) {
          "albumArtist", "albumTitle", "artist", "artworkDataType", "artworkUri", "compilation",
          "composer", "conductor", "description", "displayTitle", "genre", "mediaType", "station",
          "subtitle", "title", "writer" ->
            fieldData?.let { metadataMap.putString(field, it as String) }

          "discNumber", "recordingDay", "recordingMonth", "recordingYear", "releaseDay", "releaseMonth",
          "releaseYear", "totalDiscCount", "totalTrackCount", "trackNumber" ->
            fieldData?.let { metadataMap.putInt(field, it as Int) }

          "isBrowsable", "isPlayable" ->
            fieldData?.let { metadataMap.putBoolean(field, it as Boolean) }

          "overallRating", "userRating" ->
            fieldData?.let { metadataMap.putDouble(field, it as Double) }

//          "artworkData", "extras" ->
//            metadataMap.putNull(field)
        }
      }

      // Have fallback for `albumArtist` if it's not defined, but only when certain conditions are met.
      if (metadataMap.hasKey("albumArtist") && metadataMap.isNull("albumArtist")) {
        if (
          metadataMap.hasKey("artist") && !metadataMap.isNull("artist") &&
          metadataMap.hasKey("albumTitle") && !metadataMap.isNull("albumTitle")
          ) {
          metadataMap.putString("albumArtist", metadataMap.getString("artist"))
        }
      }

      promise.resolve(metadataMap)

    } catch (e: ExecutionException) {
      val isWantedException = e.message?.contains("androidx.media3.datasource.FileDataSource\$FileDataSourceException") ?: false
      when (isWantedException) {
        true -> promise.reject("`getMetadata` error", "File Not Found Error", e)
        false -> promise.reject("`getMetadata` error", "Unknown ExecutionException", e)
      }
    } catch (e: TrackGroupArrayException) {
      // Return default wanted metadata map where all fields are `null`.
      promise.resolve(metadataMap)
    } catch (e: Exception) {
      promise.reject("`getMetadata` error", "Metadata Retrieval Error", e)
    }
  }

  companion object {
    const val NAME = "MetadataRetriever"
  }

  override fun getName(): String = NAME
}
