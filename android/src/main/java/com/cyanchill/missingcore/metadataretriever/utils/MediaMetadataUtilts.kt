package com.cyanchill.missingcore.metadataretriever.utils

import android.util.Base64
import androidx.media3.common.MimeTypes
import androidx.media3.common.PercentageRating
import androidx.media3.common.Rating
import java.net.URLConnection

object MediaMetadataUtils {
  /** Returns a base64 image string from a `ByteArray`. */
  fun getBase64Image(bytes: ByteArray? = null, maxSizeMB: Int = 5): String? {
    if (bytes == null) return null
    // Determine the mimetype from bytes.
    val mimeType = URLConnection.guessContentTypeFromStream(bytes.inputStream())?.let {
      MimeTypes.normalizeMimeType(it)
    }
    // Ensure the mimeType we get is defined and is for an image.
    if (!MimeTypes.isImage(mimeType)) return null
    // Convert max MB to bytes. We take 3/4 of the max MB as converting a byte array to a base64
    // string causes a 33% increase in size.
    val maxSizeBytes = maxSizeMB * 0.75 * 1024 * 1024
    if (bytes.size > maxSizeBytes) return null
    return "data:$mimeType;base64,${Base64.encodeToString(bytes, Base64.DEFAULT)}"
  }

  /**
   * Convert integer picture type to a human-readable string.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata.PictureType">Link</a>
   */
  fun getID3PictureType(code: Int?): String? = when (code) {
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
   * Convert integer media type to a human-readable string.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata.MediaType">Link</a>
   */
  fun getMediaType(code: Int?): String? = when (code) {
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
  fun getPercentageRating(rating: Rating?): Double? = when (rating?.isRated()) {
    true -> PercentageRating.fromBundle(rating.toBundle()).getPercent().toDouble()
    else -> null
  }
}
