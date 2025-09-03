package com.cyanchill.missingcore.metadataretriever.modules

import com.facebook.react.bridge.ReactApplicationContext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.PercentageRating
import androidx.media3.common.Rating
import androidx.media3.common.MediaMetadata as AndroidXMediaMetadata
import androidx.media3.common.util.UnstableApi
import java.io.File
import java.io.FileOutputStream
import java.net.URLConnection
import java.util.UUID

import com.cyanchill.missingcore.metadataretriever.models.ArtworkOptions

/**
 * Utilities to format & normalize sources of metadata as a map.
 */
@OptIn(UnstableApi::class)
class MetadataReader(reactContext: ReactApplicationContext): APIConfigs() {
  private val saveDirectory = "${reactContext.cacheDir.absolutePath}${File.separator}MetadataRetriever"

  /** Create `saveDirectory` if it doesn't exist. */
  init {
    try {
      val directory = File(saveDirectory)
      if (!directory.exists()) directory.mkdirs()
    } catch (e: Exception) {}
  }

  /**
   * Relevant metadata fields found on `Format`.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format">Link</a>
   */
  fun fromFormat(format: Format): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Set `Int?` values.
    dataMap.put("bitrate", fixNoValue(format.bitrate))
    dataMap.put("channelCount", fixNoValue(format.channelCount))
    dataMap.put("sampleRate", fixNoValue(format.sampleRate))
    // Set `String?` values.
    dataMap.put("codecs", format.codecs)
    dataMap.put("sampleMimeType", format.sampleMimeType)

    return dataMap
  }

  /**
   * All metadata fields found on `MediaMetadata`.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata">Link</a>
   */
  fun fromMediaMetadata(
    mediaMetadata: AndroidXMediaMetadata,
    getArtworkData: Boolean = false,
  ): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Pre-compute values to put in hash map.
    val artworkData = if (getArtworkData) getBase64Image(mediaMetadata.artworkData) else null
    val trackNumber = if (mediaMetadata.trackNumber == 0) null else mediaMetadata.trackNumber
    val year = parseYear(mediaMetadata.recordingYear) ?: parseYear(mediaMetadata.releaseYear)

    // Set `Boolean?` values.
    dataMap.put("isBrowsable", mediaMetadata.isBrowsable)
    dataMap.put("isPlayable", mediaMetadata.isPlayable)
    // Set `Double?` values.
    dataMap.put("overallRating", getPercentageRating(mediaMetadata.overallRating))
    dataMap.put("userRating", getPercentageRating(mediaMetadata.userRating))
    // Set `Int?` values.
    dataMap.put("discNumber", mediaMetadata.discNumber)
    dataMap.put("recordingDay", mediaMetadata.recordingDay)
    dataMap.put("recordingMonth", mediaMetadata.recordingMonth)
    dataMap.put("recordingYear", parseYear(mediaMetadata.recordingYear))
    dataMap.put("releaseDay", mediaMetadata.releaseDay)
    dataMap.put("releaseMonth", mediaMetadata.releaseMonth)
    dataMap.put("releaseYear", parseYear(mediaMetadata.releaseYear))
    dataMap.put("totalDiscCount", mediaMetadata.totalDiscCount)
    dataMap.put("totalTrackCount", mediaMetadata.totalTrackCount)
    dataMap.put("trackNumber", trackNumber)
    dataMap.put("year", year)
    // Set `String?` values.
    dataMap.put("albumArtist", mediaMetadata.albumArtist?.toString())
    dataMap.put("albumTitle", mediaMetadata.albumTitle?.toString())
    dataMap.put("artist", mediaMetadata.artist?.toString())
    dataMap.put("artworkData", artworkData)
    dataMap.put("artworkDataType", getID3PictureType(mediaMetadata.artworkDataType))
    dataMap.put("artworkUri", mediaMetadata.artworkUri?.toString())
    dataMap.put("compilation", mediaMetadata.compilation?.toString())
    dataMap.put("composer", mediaMetadata.composer?.toString())
    dataMap.put("conductor", mediaMetadata.conductor?.toString())
    dataMap.put("description", mediaMetadata.description?.toString())
    dataMap.put("displayTitle", mediaMetadata.displayTitle?.toString())
    dataMap.put("genre", mediaMetadata.genre?.toString())
    dataMap.put("mediaType", getMediaType(mediaMetadata.mediaType))
    dataMap.put("station", mediaMetadata.station?.toString())
    dataMap.put("subtitle", mediaMetadata.subtitle?.toString())
    dataMap.put("title", mediaMetadata.title?.toString())
    dataMap.put("writer", mediaMetadata.writer?.toString())

    return dataMap
  }

  /**
   * Subset of metadata fields found on `MediaMetadataRetriever` that correlates with the fields on `MediaMetadata`.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata">Link</a>
   */
  fun fromMediaMetadataRetriever(
    mmr: MediaMetadataRetriever,
    getArtworkData: Boolean = false,
  ): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Pre-compute values to put in hash map.
    val artworkData = if (getArtworkData) getBase64Image(mmr.embeddedPicture) else null
    val trackNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
      ?.let { if (it.toIntOrNull() == 0) null else it.toIntOrNull() }
    val year = parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)) ?: run {
      try {
        // The "date" format should start with 4 digits representing the year.
        parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))
          ?.let { if (it > 999) it else null }
      } catch (e: Exception) {
        null
      }
    }

    // Set `Int?` values.
    dataMap.put("discNumber", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.toIntOrNull())
    dataMap.put("recordingYear", parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)))
    dataMap.put("totalTrackCount", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toIntOrNull())
    dataMap.put("trackNumber", trackNumber)
    dataMap.put("year", year)
    // Set `String?` values.
    dataMap.put("albumArtist", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST))
    dataMap.put("albumTitle", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM))
    dataMap.put("artist", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
    dataMap.put("artworkData", artworkData)
    dataMap.put("compilation", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION))
    dataMap.put("composer", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER))
    dataMap.put("genre", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE))
    dataMap.put("mediaType", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE))
    dataMap.put("title", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
    dataMap.put("writer", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER))

    return dataMap
  }

  //#region [Artwork Utils]
  /** Returns a base64 image string from a `ByteArray`. */
  fun getBase64Image(bytes: ByteArray? = null): String? {
    if (bytes == null) return null
    // Determine the mimetype from bytes.
    val mimeType = URLConnection.guessContentTypeFromStream(bytes.inputStream())?.let {
      MimeTypes.normalizeMimeType(it)
    }
    // Ensure the mimeType we get is defined and is for an image.
    if (!MimeTypes.isImage(mimeType)) return null
    // Convert max MB to bytes. We take 3/4 of the max MB as converting a byte array to a base64
    // string causes a 33% increase in size.
    val maxSizeMB = apiConfigs.getDouble(MAX_IMAGE_SIZE_MB, 5.0)
    val maxSizeBytes = maxSizeMB * 0.75 * 1024 * 1024
    if (bytes.size > maxSizeBytes) return null
    return "data:$mimeType;base64,${Base64.encodeToString(bytes, Base64.DEFAULT)}"
  }

  /** Save `ByteArray` as image, returning the URI if it was saved correctly. */
  fun saveImage(bytes: ByteArray, options: ArtworkOptions): String? {
    try {
      // Generate path to save image if we didn't provide one.
      val imgUri = options.saveUri ?: "$saveDirectory${File.separator}${UUID.randomUUID()}${options.format.fileExtension}"
      val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
      FileOutputStream(imgUri).use { fos ->
        bitmap.compress(
          options.format.compressFormat,
          (options.compress * 100).toInt(),
          fos,
        )
        fos.flush()
      }
      return Uri.fromFile(File(imgUri)).toString()
    } catch (e: Exception) {
      return null
    }
  }
  //#endregion

  //#region [Internal Helpers To Parse Metadata Values]
  /**
   * Return `null` if we see `Format.NO_VALUE` (-1).
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format#NO_VALUE()">Link</a>
   */
  private fun fixNoValue(intVal: Int?) = if (intVal == Format.NO_VALUE) null else intVal

  /**
   * Convert integer picture type to a human-readable string.
   *
   * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata.PictureType">Link</a>
   */
  private fun getID3PictureType(code: Int?) = when (code) {
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
  private fun getMediaType(code: Int?) = when (code) {
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
  private fun getPercentageRating(rating: Rating?): Double? {
    // Kotlin's interoperability with Java lets `get*` & `is*` functions to be accessed via property syntax.
    return when (rating?.isRated) {
      true -> PercentageRating.fromBundle(rating.toBundle()).percent.toDouble()
      else -> null
    }
  }

  /** Returns the year from ISO 8601 format (ie: `YYYY-MM-DD`). */
  private fun parseYear(dateTime: Any?): Int? {
    if (dateTime == null) return null
    val dateTimeString = dateTime.toString() // We expect `dateTime` to be a `String` or `Int`.
    if (dateTimeString.length < 4) return null
    return dateTimeString.substring(0, 4).toIntOrNull()
  }
  //#endregion
}
