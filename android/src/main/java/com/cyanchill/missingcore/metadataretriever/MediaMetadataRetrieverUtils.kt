package com.cyanchill.missingcore.metadataretriever

import android.media.MediaMetadataRetriever


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
  "discNumber" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.toInt() // Returns `Int?`
  "displayTitle" -> null
//  "extras" -> metadataMap.putString()
  "genre" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
  "isBrowsable" -> null // Returns `Boolean?`
  "isPlayable" -> null // Returns `Boolean?`
  "mediaType" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) // Returns mimeType
  "overallRating" -> null // Returns `Double?`
  "recordingDay" -> null // Returns `Int?`
  "recordingMonth" -> null // Returns `Int?`
  "recordingYear" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toInt() // Returns `Int?`
  "releaseDay" -> null // Returns `Int?`
  "releaseMonth" -> null // Returns `Int?`
  "releaseYear" -> null // Returns `Int?`
  "station" -> null
  "subtitle" -> null
  "title" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
  "totalDiscCount" -> null // Returns `Int?`
  "totalTrackCount" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toInt() // Returns `Int?`
  "trackNumber" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let {
    // `trackNumber` seems to default to `0`, which is incorrect if it was `undefined` in reality.
    val trackNumber = it.toInt()
    return if (trackNumber == 0) null else trackNumber
  } // Returns `Int?`
  "userRating" -> null // Returns `Double?`
  "writer" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
  "year" -> mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toInt() ?: run {
    val date: String? = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
    try {
      // The "date" format should start with 4 digits representing the year.
      date?.substring(0, 4)?.toInt()?.also { if (it > 999) it else null }
    } catch (err: Exception) {
      null
    }
  } // Returns `Int?`
  else -> null
}
