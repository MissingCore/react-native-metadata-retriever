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
import androidx.media3.common.PercentageRating
import androidx.media3.common.Rating
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray


class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    // Populate return object with default values based on input.
    val metadataMap = Arguments.createMap()
    for (i in 0 until options.size()) {
      metadataMap.putNull(options.getString(i))
    }

    // Get static metadata of media from its uri.
    // See https://developer.android.com/media/media3/exoplayer/retrieving-metadata#kotlin
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
      // Use scope functions to help determine output.
      // See https://kotlinlang.org/docs/scope-functions.html
      when (field) {
        "albumArtist" -> mediaMetadata.albumArtist?.let { metadataMap.putString(field, it.toString()) }
        "albumTitle" -> mediaMetadata.albumTitle?.let { metadataMap.putString(field, it.toString()) }
        "artist" -> mediaMetadata.artist?.let { metadataMap.putString(field, it.toString()) }
//        "artworkData" -> metadataMap.putString()
        "artworkDataType" -> mediaMetadata.artworkDataType?.let { metadataMap.putString(field, getPictureTypeString(it)) }
        "artworkUri" -> mediaMetadata.artworkUri?.let { metadataMap.putString(field, it.toString()) }
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
        "mediaType" -> mediaMetadata.mediaType?.let { metadataMap.putString(field, getMediaTypeString(it)) }
        "overallRating" -> mediaMetadata.overallRating?.let { metadataMap.putDouble(field, getPercentageRatingRating(it)) }
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
        "userRating" -> mediaMetadata.userRating?.let { metadataMap.putDouble(field, getPercentageRatingRating(it)) }
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

  // Convert the picture type code into a human-readable string.
  // SEE https://developer.android.com/reference/androidx/media3/common/MediaMetadata.PictureType
  fun getPictureTypeString(code: Int): String? {
    return when (code) {
      0 -> "Other"
      1 -> "32x32 pixels 'file icon' (PNG only)"
      2 -> "Other file icon"
      3 -> "Cover (front)"
      4 -> "Cover (back)"
      5 -> "Leaflet page"
      6 -> "Media (e.g. label side of CD)"
      7 -> "Lead artist/lead performer/soloist"
      8 -> "Artist/performer"
      9 -> "Conductor"
      10 -> "Band/Orchestra"
      11 -> "Composer"
      12 -> "Lyricist/text writer"
      13 -> "Recording Location"
      14 -> "During recording"
      15 -> "During performance"
      16 -> "Movie/video screen capture"
      17 -> "A bright coloured fish"
      18 -> "Illustration"
      19 -> "Band/artist logotype"
      20 -> "Publisher/Studio logotype"
      else -> null
    }
  }

  // Convert the media type code into a human-readable string.
  // SEE https://developer.android.com/reference/androidx/media3/common/MediaMetadata.MediaType
  fun getMediaTypeString(code: Int): String? {
    return when (code) {
      0 -> "Mixed"
      1 -> "Music"
      2 -> "Audio book chapter"
      3 -> "Podcast episode"
      4 -> "Radio station"
      5 -> "News"
      6 -> "Video"
      7 -> "Trailer"
      8 -> "Movie"
      9 -> "TV show"
      10 -> "Album"
      11 -> "Artist"
      12 -> "Genre"
      13 -> "Playlist"
      14 -> "Year"
      15 -> "Audio book"
      16 -> "Podcast"
      17 -> "TV channel"
      18 -> "TV series"
      19 -> "TV season"
      20 -> "Folder mixed"
      21 -> "Folder albums"
      22 -> "Folder artists"
      23 -> "Folder genres"
      24 -> "Folder playlists"
      25 -> "Folder years"
      26 -> "Folder audio books"
      27 -> "Folder podcasts"
      28 -> "Folder tv channels"
      29 -> "Folder tv series"
      30 -> "Folder tv shows"
      31 -> "Folder radio stations"
      32 -> "Folder news"
      33 -> "Folder videos"
      34 -> "Folder trailers"
      35 -> "Folder movies"
      else -> null
    }
  }

  // Get the percentage rating from a `Rating`.
  // SEE https://developer.android.com/reference/androidx/media3/common/Rating
  fun getPercentageRatingRating(rating: Rating): Double {
    return when (rating.isRated()) {
      false -> -1.0
      true -> PercentageRating.fromBundle(rating.toBundle()).getPercent().toDouble()
    }
  }

  companion object {
    const val NAME = "MetadataRetriever"
  }
}
