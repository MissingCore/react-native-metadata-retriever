package com.cyanchill.missingcore.metadataretriever.models

import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata as AndroidXMediaMetadata
import androidx.media3.common.util.UnstableApi

import com.cyanchill.missingcore.metadataretriever.utils.MediaMetadataUtils
import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils
// FIXME: Remove temporary import
import com.cyanchill.missingcore.metadataretriever.readMMRField

@OptIn(UnstableApi::class)
data class NormalizedMediaMetadataItem(
  val mediaMetadata: AndroidXMediaMetadata,
  val uri: String,
  val getArtworkData: Boolean = false,
) : MediaMetadata {
  override val albumArtist = mediaMetadata.albumArtist?.toString()
  override val albumTitle = mediaMetadata.albumTitle?.toString()
  override val artist = mediaMetadata.artist?.toString()
  override val artworkData = run {
    if (getArtworkData) MediaMetadataUtils.getBase64Image(mediaMetadata.artworkData)
    else null
  }
  override val artworkDataType = MediaMetadataUtils.getID3PictureType(mediaMetadata.artworkDataType)
  override val artworkUri = mediaMetadata.artworkUri?.toString()
  override val compilation = mediaMetadata.compilation?.toString()
  override val composer = mediaMetadata.composer?.toString()
  override val conductor = mediaMetadata.conductor?.toString()
  override val description = mediaMetadata.description?.toString()
  override val discNumber = mediaMetadata.discNumber
  override val displayTitle = mediaMetadata.displayTitle?.toString()
  // override val extras: Any?
  override val genre = mediaMetadata.genre?.toString()
  override val isBrowsable = mediaMetadata.isBrowsable
  override val isPlayable = mediaMetadata.isPlayable
  override val mediaType = MediaMetadataUtils.getMediaType(mediaMetadata.mediaType)
  override val overallRating = MediaMetadataUtils.getPercentageRating(mediaMetadata.overallRating)
  override val recordingDay = mediaMetadata.recordingDay
  override val recordingMonth = mediaMetadata.recordingMonth
  override val recordingYear = NormalizationUtils.parseYear(mediaMetadata.recordingYear)
  override val releaseDay = mediaMetadata.releaseDay
  override val releaseMonth = mediaMetadata.releaseMonth
  override val releaseYear = NormalizationUtils.parseYear(mediaMetadata.releaseYear)
  override val station = mediaMetadata.station?.toString()
  override val subtitle = mediaMetadata.subtitle?.toString()
  override val title = mediaMetadata.title?.toString()
  override val totalDiscCount = mediaMetadata.totalDiscCount
  override val totalTrackCount = mediaMetadata.totalTrackCount
  override val trackNumber = run {
    if (mediaMetadata.trackNumber == 0) null
    else mediaMetadata.trackNumber
  }
  override val userRating = MediaMetadataUtils.getPercentageRating(mediaMetadata.userRating)
  override val writer = mediaMetadata.writer?.toString()
  /* List of custom fields derived from other fields. */
  override val year = NormalizationUtils.parseYear(mediaMetadata.recordingYear)
    ?: NormalizationUtils.parseYear(mediaMetadata.releaseYear)
    ?: run {
      val mmrMetadata = MediaMetadataRetriever()
      mmrMetadata.setDataSource(NormalizationUtils.getSafeUri(uri))
      readMMRField(mmrMetadata, "year") as Int?
    }
}
