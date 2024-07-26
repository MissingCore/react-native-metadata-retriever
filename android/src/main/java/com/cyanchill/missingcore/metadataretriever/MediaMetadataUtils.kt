package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext

import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.common.PercentageRating
import androidx.media3.common.Rating


/**
 * Returns a list of `Format` from an uri from a process involving `MetadataRetriever.retrieveMetadata()`.
 *
 * @throws ExecutionException If file was not found from uri.
 * @throws TrackGroupArrayException If no tracks were found in media provided by the uri.
 *
 * @see <a href="https://developer.android.com/media/media3/exoplayer/retrieving-metadata#wo-playback">Link</a>
 */
fun getFormatList(context: ReactApplicationContext, uri: String): List<Format> {
  // Get static metadata of media from its uri.
  // See https://developer.android.com/media/media3/exoplayer/retrieving-metadata#kotlin
  val mediaItem = MediaItem.fromUri(uri)
  val trackGroupArray = MetadataRetriever.retrieveMetadata(context, mediaItem).get()

  if (trackGroupArray == null) throw TrackGroupArrayException()

  // Unwrap the containers returned by `MetadataRetriever.retrieveMetadata`, getting a list
  // of `Format` from audio `TrackGroup`.
  val formatList = mutableListOf<Format>()
  for (i in 0 until trackGroupArray.length) {
    val trackGroup = trackGroupArray[i]
    // Only look at the track group containing audio.
    if (trackGroup.type != C.TRACK_TYPE_AUDIO) continue
    for (j in 0 until trackGroup.length) {
      // By definition, a `TrackGroup` should have at least 1 `Format`.
      // SEE https://developer.android.com/reference/androidx/media3/common/TrackGroup#TrackGroup(androidx.media3.common.Format...)
      formatList.add(trackGroup.getFormat(j))
    }
  }

  return formatList
}

/** Returns a list of `Metadata` from `List<Format>`. */
fun getMetadataListFromFormatList(formatList: List<Format>): List<Metadata> {
  val metadataList = mutableListOf<Metadata>()
  formatList.forEach {
    it.metadata?.let { metadataList.add(it) }
  }
  return metadataList
}

/**
 * Convert "picture type code" into a human-readable string.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata.PictureType">Link</a>
 */
fun getPictureTypeString(code: Int?): String? = when (code) {
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

/**
 * Convert "media type code" into a human-readable string.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata.MediaType">Link</a>
 */
fun getMediaTypeString(code: Int?): String? = when (code) {
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

/**
 * Get the percentage rating from a `Rating`.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/Rating">Link</a>
 */
fun getPercentageRatingRating(rating: Rating?): Double? = when (rating?.isRated()) {
  true -> PercentageRating.fromBundle(rating.toBundle()).getPercent().toDouble()
  else -> null
}

/**
 * Return `null` if we see `Format.NO_VALUE` (-1).
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format#NO_VALUE()">Link</a>
 */
fun fixNoValue(intVal: Int?): Int? = when (intVal) {
  null, Format.NO_VALUE -> null
  else -> intVal
}

/**
 * Dynamically access a public field inside a `Format` instance.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format">Link</a>
 */
fun readFormatField(format: Format, field: String): Any? = when (field) {
  "bitrate" -> fixNoValue(format.bitrate) // Returns `Int?`
  "channelCount" -> fixNoValue(format.channelCount) // Returns `Int?`
  "codecs" -> format.codecs
  "sampleMimeType" -> format.sampleMimeType
  "sampleRate" -> fixNoValue(format.sampleRate) // Returns `Int?`
  else -> null
}

/**
 * Dynamically access a public field inside a `MediaMetadata` instance.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata">Link</a>
 */
fun readMediaMetadataField(mediaMetadata: MediaMetadata, field: String): Any? = when (field) {
  "albumArtist" -> mediaMetadata.albumArtist?.toString()
  "albumTitle" -> mediaMetadata.albumTitle?.toString()
  "artist" -> mediaMetadata.artist?.toString()
  "artworkData" -> getBase64Image(mediaMetadata.artworkData)
  "artworkDataType" -> getPictureTypeString(mediaMetadata.artworkDataType)
  "artworkUri" -> mediaMetadata.artworkUri?.toString()
  "compilation" -> mediaMetadata.compilation?.toString()
  "composer" -> mediaMetadata.composer?.toString()
  "conductor" -> mediaMetadata.conductor?.toString()
  "description" -> mediaMetadata.description?.toString()
  "discNumber" -> mediaMetadata.discNumber // Returns `Int?`
  "displayTitle" -> mediaMetadata.displayTitle?.toString()
//  "extras" -> metadataMap.putString()
  "genre" -> mediaMetadata.genre?.toString()
  "isBrowsable" -> mediaMetadata.isBrowsable // Returns `Boolean?`
  "isPlayable" -> mediaMetadata.isPlayable // Returns `Boolean?`
  "mediaType" -> getMediaTypeString(mediaMetadata.mediaType)
  "overallRating" -> getPercentageRatingRating(mediaMetadata.overallRating) // Returns `Double?`
  "recordingDay" -> mediaMetadata.recordingDay // Returns `Int?`
  "recordingMonth" -> mediaMetadata.recordingMonth // Returns `Int?`
  "recordingYear" -> mediaMetadata.recordingYear // Returns `Int?`
  "releaseDay" -> mediaMetadata.releaseDay // Returns `Int?`
  "releaseMonth" -> mediaMetadata.releaseMonth // Returns `Int?`
  "releaseYear" -> mediaMetadata.releaseYear // Returns `Int?`
  "station" -> mediaMetadata.station?.toString()
  "subtitle" -> mediaMetadata.subtitle?.toString()
  "title" -> mediaMetadata.title?.toString()
  "totalDiscCount" -> mediaMetadata.totalDiscCount // Returns `Int?`
  "totalTrackCount" -> mediaMetadata.totalTrackCount // Returns `Int?`
  "trackNumber" -> mediaMetadata.trackNumber // Returns `Int?`
  "userRating" -> getPercentageRatingRating(mediaMetadata.userRating) // Returns `Double?`
  "writer" -> mediaMetadata.writer?.toString()
  else -> null
}
