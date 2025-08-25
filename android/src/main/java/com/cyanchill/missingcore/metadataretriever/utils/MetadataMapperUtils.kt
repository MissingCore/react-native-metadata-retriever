package com.cyanchill.missingcore.metadataretriever.utils

import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.MediaMetadata as AndroidXMediaMetadata
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
object MetadataMapperUtils {
  fun mapFormatMetadata(format: Format): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Set `Int?` values.
    dataMap.put("bitrate", NormalizationUtils.fixNoValue(format.bitrate))
    dataMap.put("channelCount", NormalizationUtils.fixNoValue(format.channelCount))
    dataMap.put("sampleRate", NormalizationUtils.fixNoValue(format.sampleRate))
    // Set `String?` values.
    dataMap.put("codecs", format.codecs)
    dataMap.put("sampleMimeType", format.sampleMimeType)

    return dataMap
  }

  fun mapMediaMetadata(
    mediaMetadata: AndroidXMediaMetadata,
    getArtworkData: Boolean = false,
  ): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Pre-compute values to put in hash map.
    val artworkData = if (getArtworkData) MediaMetadataUtils.getBase64Image(mediaMetadata.artworkData) else null
    val trackNumber = if (mediaMetadata.trackNumber == 0) null else mediaMetadata.trackNumber
    val year = NormalizationUtils.parseYear(mediaMetadata.recordingYear)
      ?: NormalizationUtils.parseYear(mediaMetadata.releaseYear)

    // Set `Boolean?` values.
    dataMap.put("isBrowsable", mediaMetadata.isBrowsable)
    dataMap.put("isPlayable", mediaMetadata.isPlayable)
    // Set `Double?` values.
    dataMap.put("overallRating", MediaMetadataUtils.getPercentageRating(mediaMetadata.overallRating))
    dataMap.put("userRating", MediaMetadataUtils.getPercentageRating(mediaMetadata.userRating))
    // Set `Int?` values.
    dataMap.put("discNumber", mediaMetadata.discNumber)
    dataMap.put("recordingDay", mediaMetadata.recordingDay)
    dataMap.put("recordingMonth", mediaMetadata.recordingMonth)
    dataMap.put("recordingYear", NormalizationUtils.parseYear(mediaMetadata.recordingYear))
    dataMap.put("releaseDay", mediaMetadata.releaseDay)
    dataMap.put("releaseMonth", mediaMetadata.releaseMonth)
    dataMap.put("releaseYear", NormalizationUtils.parseYear(mediaMetadata.releaseYear))
    dataMap.put("totalDiscCount", mediaMetadata.totalDiscCount)
    dataMap.put("totalTrackCount", mediaMetadata.totalTrackCount)
    dataMap.put("trackNumber", trackNumber)
    dataMap.put("year", year)
    // Set `String?` values.
    dataMap.put("albumArtist", mediaMetadata.albumArtist?.toString())
    dataMap.put("albumTitle", mediaMetadata.albumTitle?.toString())
    dataMap.put("artist", mediaMetadata.artist?.toString())
    dataMap.put("artworkData", artworkData)
    dataMap.put("artworkDataType", MediaMetadataUtils.getID3PictureType(mediaMetadata.artworkDataType))
    dataMap.put("artworkUri", mediaMetadata.artworkUri?.toString())
    dataMap.put("compilation", mediaMetadata.compilation?.toString())
    dataMap.put("composer", mediaMetadata.composer?.toString())
    dataMap.put("conductor", mediaMetadata.conductor?.toString())
    dataMap.put("description", mediaMetadata.description?.toString())
    dataMap.put("displayTitle", mediaMetadata.displayTitle?.toString())
    dataMap.put("genre", mediaMetadata.genre?.toString())
    dataMap.put("mediaType", MediaMetadataUtils.getMediaType(mediaMetadata.mediaType))
    dataMap.put("station", mediaMetadata.station?.toString())
    dataMap.put("subtitle", mediaMetadata.subtitle?.toString())
    dataMap.put("title", mediaMetadata.title?.toString())
    dataMap.put("writer", mediaMetadata.writer?.toString())

    return dataMap
  }

  fun mapMediaMetadataRetriever(
    mmr: MediaMetadataRetriever,
    getArtworkData: Boolean = false,
  ): HashMap<String, Any?> {
    val dataMap = hashMapOf<String, Any?>()

    // Pre-compute values to put in hash map.
    val artworkData = if (getArtworkData) MediaMetadataUtils.getBase64Image(mmr.getEmbeddedPicture()) else null
    val trackNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
      ?.let { if (it.toIntOrNull() == 0) null else it.toIntOrNull() }
    val year = NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)) ?: run {
      try {
        // The "date" format should start with 4 digits representing the year.
        NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))
          ?.let { if (it > 999) it else null }
      } catch (err: Exception) {
        null
      }
    }

    // Set `Int?` values.
    dataMap.put("discNumber", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.toIntOrNull())
    dataMap.put("recordingYear", NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)))
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
}
