@file:JvmName("MediaMetadataUtils")
package com.missingcore.metadataretriever

import androidx.media3.common.MediaMetadata
import androidx.media3.common.PercentageRating
import androidx.media3.common.Rating


/**
 * Convert "picture type code" into a human-readable string.
 *
 * SEE https://developer.android.com/reference/androidx/media3/common/MediaMetadata.PictureType
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
 * SEE https://developer.android.com/reference/androidx/media3/common/MediaMetadata.MediaType
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
 * SEE https://developer.android.com/reference/androidx/media3/common/Rating
 */
fun getPercentageRatingRating(rating: Rating?): Double? = when (rating?.isRated()) {
  false -> -1.0
  true -> PercentageRating.fromBundle(rating.toBundle()).getPercent().toDouble()
  else -> null
}

/**
 * Dynamically access a public field inside a `MediaMetadata` instance.
 *
 * SEE https://developer.android.com/reference/androidx/media3/common/MediaMetadata
 */
fun readMediaMetadataField(mediaMetadata: MediaMetadata, field: String): Any? = when (field) {
  "albumArtist" -> mediaMetadata.albumArtist?.toString()
  "albumTitle" -> mediaMetadata.albumTitle?.toString()
  "artist" -> mediaMetadata.artist?.toString()
//  "artworkData" -> metadataMap.putString()
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
