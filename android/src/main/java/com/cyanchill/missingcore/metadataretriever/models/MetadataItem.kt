package com.cyanchill.missingcore.metadataretriever.models

/**
 * Metadata fields found on `Format` that has been normalized for our use.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format">Link</a>
 */
interface FormatMetadata {
  val bitrate: Int?
  val channelCount: Int?
  val codecs: String?
  val sampleMimeType: String?
  val sampleRate: Int? // in `Hz`
}

/**
 * Metadata fields found on `MediaMetadata` that has been normalized for our use.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/MediaMetadata">Link</a>
 */
interface MediaMetadata {
  val albumArtist: String?
  val albumTitle: String?
  val artist: String?
  val artworkData: String?
  val artworkDataType: String?
  val artworkUri: String?
  val compilation: String?
  val composer: String?
  val conductor: String?
  val description: String?
  val discNumber: Int?
  val displayTitle: String?
  // val extras: Any?
  val genre: String?
  val isBrowsable: Boolean?
  val isPlayable: Boolean?
  val mediaType: String?
  val overallRating: Double?
  val recordingDay: Int?
  val recordingMonth: Int?
  val recordingYear: Int?
  val releaseDay: Int?
  val releaseMonth: Int?
  val releaseYear: Int?
  val station: String?
  val subtitle: String?
  val title: String?
  val totalDiscCount: Int?
  val totalTrackCount: Int?
  val trackNumber: Int?
  val userRating: Double?
  val writer: String?
  /* List of custom fields derived from other fields. */
  val year: Int?
}

interface MetadataItem : FormatMetadata, MediaMetadata
