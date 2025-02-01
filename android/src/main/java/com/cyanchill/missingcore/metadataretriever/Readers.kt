package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext

import android.media.MediaMetadataRetriever
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.exoplayer.MetadataRetriever


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
  val mediaItem = MediaItem.fromUri(getSafeUri(uri))
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
 * Dynamically access a public field inside a `MediaMetadata` instance. The `uri` parameter is only
 * used if we specify the `year` field and if none of the year-related fields in `MediaMetadata`
 * produces a valid result.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata">Link</a>
 */
fun readMediaMetadataField(mediaMetadata: MediaMetadata, field: String, uri: String): Any? = when (field) {
  "albumArtist" -> mediaMetadata.albumArtist?.toString()
  "albumTitle" -> mediaMetadata.albumTitle?.toString()
  "artist" -> mediaMetadata.artist?.toString()
  "artworkData" -> getBase64Image(mediaMetadata.artworkData)
  "artworkDataType" -> getID3PictureType(mediaMetadata.artworkDataType)
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
  "mediaType" -> getMediaType(mediaMetadata.mediaType)
  "overallRating" -> getPercentageRating(mediaMetadata.overallRating) // Returns `Double?`
  "recordingDay" -> mediaMetadata.recordingDay // Returns `Int?`
  "recordingMonth" -> mediaMetadata.recordingMonth // Returns `Int?`
  "recordingYear" -> parseYear(mediaMetadata.recordingYear) // Returns `Int?`
  "releaseDay" -> mediaMetadata.releaseDay // Returns `Int?`
  "releaseMonth" -> mediaMetadata.releaseMonth // Returns `Int?`
  "releaseYear" -> parseYear(mediaMetadata.releaseYear) // Returns `Int?`
  "station" -> mediaMetadata.station?.toString()
  "subtitle" -> mediaMetadata.subtitle?.toString()
  "title" -> mediaMetadata.title?.toString()
  "totalDiscCount" -> mediaMetadata.totalDiscCount // Returns `Int?`
  "totalTrackCount" -> mediaMetadata.totalTrackCount // Returns `Int?`
  "trackNumber" -> mediaMetadata.trackNumber // Returns `Int?`
  "userRating" -> getPercentageRating(mediaMetadata.userRating) // Returns `Double?`
  "writer" -> mediaMetadata.writer?.toString()
  "year" -> parseYear(mediaMetadata.recordingYear) ?: parseYear(mediaMetadata.releaseYear) ?: run {
    val mmrMetadata = MediaMetadataRetriever()
    mmrMetadata.setDataSource(getSafeUri(uri))
    readMMRField(mmrMetadata, "year")
  } // Returns `Int?`
  else -> null
}

/**
 * Dynamically access a public field inside a `MediaMetadataRetriever` instance.
 *
 * @see <a href="https://developer.android.com/reference/android/media/MediaMetadataRetriever">Link</a>
 */
fun readMMRField(mmr: MediaMetadataRetriever, field: String): Any? = when (field) {
  "albumArtist" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
  "albumTitle" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
  "artist" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
  "artworkData" -> getBase64Image(mmr.getEmbeddedPicture())
  "artworkDataType" -> null
  "artworkUri" -> null
  "compilation" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION)
  "composer" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
  "conductor" -> null
  "description" -> null
  "discNumber" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.toIntOrNull() // Returns `Int?`
  "displayTitle" -> null
//  "extras" -> metadataMap.putString()
  "genre" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
  "isBrowsable" -> null // Returns `Boolean?`
  "isPlayable" -> null // Returns `Boolean?`
  "mediaType" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) // Returns mimeType
  "overallRating" -> null // Returns `Double?`
  "recordingDay" -> null // Returns `Int?`
  "recordingMonth" -> null // Returns `Int?`
  "recordingYear" -> parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)) // Returns `Int?`
  "releaseDay" -> null // Returns `Int?`
  "releaseMonth" -> null // Returns `Int?`
  "releaseYear" -> null // Returns `Int?`
  "station" -> null
  "subtitle" -> null
  "title" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
  "totalDiscCount" -> null // Returns `Int?`
  "totalTrackCount" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toIntOrNull() // Returns `Int?`
  "trackNumber" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let {
    // `trackNumber` seems to default to `0`, which is incorrect if it was `undefined` in reality.
    val trackNumber = it.toIntOrNull() ?: 0
    return if (trackNumber == 0) null else trackNumber
  } // Returns `Int?`
  "userRating" -> null // Returns `Double?`
  "writer" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
  "year" -> parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)) ?: run {
    try {
      // The "date" format should start with 4 digits representing the year.
      parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))?.let { if (it > 999) it else null }
    } catch (err: Exception) {
      null
    }
  } // Returns `Int?`
  else -> null
}
