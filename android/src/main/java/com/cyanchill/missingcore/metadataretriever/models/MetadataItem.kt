package com.cyanchill.missingcore.metadataretriever.model

interface MetadataItem {
  /* List of fields available on `Format`. */
  var bitrate: Int?
  var channelCount: Int?
  var codecs: String?
  var sampleMimeType: String?
  var sampleRate: Int? // in `Hz`
  /* List of fields available on `MediaMetadata`. */
  var albumArtist: String?
  var albumTitle: String?
  var artist: String?
  var artworkData: String?
  var artworkDataType: String?
  var artworkUri: String?
  var compilation: String?
  var composer: String?
  var conductor: String?
  var description: String?
  var discNumber: Int?
  var displayTitle: String?
  // var extras: Any?
  var genre: String?
  var isBrowsable: Boolean?
  var isPlayable: Boolean?
  var mediaType: String?
  var overallRating: Double?
  var recordingDay: Int?
  var recordingMonth: Int?
  var recordingYear: Int?
  var releaseDay: Int?
  var releaseMonth: Int?
  var releaseYear: Int?
  var station: String?
  var subtitle: String?
  var title: String?
  var totalDiscCount: Int?
  var totalTrackCount: Int?
  var trackNumber: Int?
  var userRating: Double?
  var writer: String?
  /* List of custom fields derived from other fields. */
  var year: Int?
}
