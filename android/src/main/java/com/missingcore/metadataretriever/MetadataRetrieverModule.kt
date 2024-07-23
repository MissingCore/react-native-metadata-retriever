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
import androidx.media3.exoplayer.source.TrackGroupArray


class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  override fun getName(): String {
    return NAME
  }

  override fun getTypedExportedConstants(): Map<String, Any?> {
    val constants: MutableMap<String, Any?> = HashMap()
    return constants
  }

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    // Populate return object with default values based on input.
    val metadataMap = Arguments.createMap()
    for (i in 0 until options.size()) {
      metadataMap.putNull(options.getString(i))
    }

    // Get static metadata of media from its uri.
    val mediaItem = MediaItem.fromUri(uri)
    var trackGroupArray: TrackGroupArray? = null
    try {
      trackGroupArray = MetadataRetriever.retrieveMetadata(context, mediaItem).get()
    } catch (err: Throwable) {
      promise.reject("Metadata Retrieval Error", err)
      return
    }

    // Return map where wanted metadata is all `null`.
    if (trackGroupArray == null) {
      promise.resolve(metadataMap)
      return
    }

    // Start unwrapping the containers returned by `MetadataRetriever.retrieveMetadata` to get
    // access to the metadata.
    var metadataList = mutableListOf<Metadata>()
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
      when (field) {
        "albumArtist" -> mediaMetadata.albumArtist?.let { metadataMap.putString(field, it.toString()) }
        "albumTitle" -> mediaMetadata.albumTitle?.let { metadataMap.putString(field, it.toString()) }
        "artist" -> mediaMetadata.artist?.let { metadataMap.putString(field, it.toString()) }
//        "artworkData" -> metadataMap.putString()
//        "artworkDataType" -> metadataMap.putString()
//        "artworkUri" -> metadataMap.putString()
        "compilation" -> mediaMetadata.compilation?.let { metadataMap.putString(field, it.toString()) }
        "composer" -> mediaMetadata.composer?.let { metadataMap.putString(field, it.toString()) }
        "conductor" -> mediaMetadata.conductor?.let { metadataMap.putString(field, it.toString()) }
        "description" -> mediaMetadata.description?.let { metadataMap.putString(field, it.toString()) }
        "discNumber" -> mediaMetadata.discNumber?.let { metadataMap.putInt(field, it) }
        "displayTitle" -> mediaMetadata.displayTitle?.let { metadataMap.putString(field, it.toString()) }
//        "extras" -> metadataMap.putString()
        "genre" -> mediaMetadata.genre?.let { metadataMap.putString(field, it.toString()) }
        "isBrowsable" -> mediaMetadata.isBrowsable?.let { metadataMap.putBoolean(field, it) }
        "isPlayable" -> mediaMetadata.isPlayable?.let { metadataMap.putBoolean(field, it) }
//        "mediaType" -> metadataMap.putString()
//        "overallRating" -> metadataMap.putString()
        "recordingDay" -> mediaMetadata.recordingDay?.let { metadataMap.putInt(field, it) }
        "recordingMonth" -> mediaMetadata.recordingMonth?.let { metadataMap.putInt(field, it) }
        "recordingYear" -> mediaMetadata.recordingYear?.let { metadataMap.putInt(field, it) }
        "releaseDay" -> mediaMetadata.releaseDay?.let { metadataMap.putInt(field, it) }
        "releaseMonth" -> mediaMetadata.releaseMonth?.let { metadataMap.putInt(field, it) }
        "releaseYear" -> mediaMetadata.releaseYear?. let { metadataMap.putInt(field, it) }
        "station" -> mediaMetadata.station?.let { metadataMap.putString(field, it.toString()) }
        "subtitle" -> mediaMetadata.subtitle?.let { metadataMap.putString(field, it.toString()) }
        "title" -> mediaMetadata.title?.let { metadataMap.putString(field, it.toString()) }
        "totalDiscCount" -> mediaMetadata.totalDiscCount?.let { metadataMap.putInt(field, it) }
        "totalTrackCount" -> mediaMetadata.totalTrackCount?.let { metadataMap.putInt(field, it) }
        "trackNumber" -> mediaMetadata.trackNumber?.let { metadataMap.putInt(field, it) }
//        "userRating" -> metadataMap.putString()
        "writer" -> mediaMetadata.writer?.let { metadataMap.putString(field, it.toString()) }
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
  }

  companion object {
    const val NAME = "MetadataRetriever"
  }
}
