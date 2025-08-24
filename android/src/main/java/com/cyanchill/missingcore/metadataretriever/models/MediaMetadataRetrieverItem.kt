package com.cyanchill.missingcore.metadataretriever.models

import android.media.MediaMetadataRetriever

import com.cyanchill.missingcore.metadataretriever.utils.MediaMetadataUtils
import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils

data class MediaMetadataRetrieverItem(
  val mmr: MediaMetadataRetriever,
  val getArtworkData: Boolean = false,
) : MediaMetadata {
  override val albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
  override val albumTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
  override val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
  override val artworkData = run {
    if (getArtworkData) MediaMetadataUtils.getBase64Image(mmr.getEmbeddedPicture())
    else null
  }
  override val artworkDataType = null
  override val artworkUri = null
  override val compilation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION)
  override val composer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
  override val conductor = null
  override val description = null
  override val discNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.toIntOrNull()
  override val displayTitle = null
  // override val extras = null
  override val genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
  override val isBrowsable = null
  override val isPlayable = null
  override val mediaType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
  override val overallRating = null
  override val recordingDay = null
  override val recordingMonth = null
  override val recordingYear = NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR))
  override val releaseDay = null
  override val releaseMonth = null
  override val releaseYear = null
  override val station = null
  override val subtitle = null
  override val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
  override val totalDiscCount = null
  override val totalTrackCount = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toIntOrNull()
  override val trackNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let {
    // `trackNumber` seems to default to `0`, which is incorrect if it was `undefined` in reality.
    val trackNumber = it.toIntOrNull() ?: 0
    if (trackNumber == 0) null else trackNumber
  }
  override val userRating = null
  override val writer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
  /* List of custom fields derived from other fields. */
  override val year = NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)) ?: run {
    try {
      // The "date" format should start with 4 digits representing the year.
      NormalizationUtils.parseYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))?.let { if (it > 999) it else null }
    } catch (err: Exception) {
      null
    }
  }
}
