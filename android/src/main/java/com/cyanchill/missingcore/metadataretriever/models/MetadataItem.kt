package com.cyanchill.missingcore.metadataretriever.model

interface MetadataItem {
  /* List of fields available on `Format`. */
  var bitrate: number?
  var channelCount: number?
  var codecs: string?
  var sampleMimeType: string?
  var sampleRate: number? // in `Hz`
  /* List of fields available on `MediaMetadata`. */
  var albumArtist: string?
  var albumTitle: string?
  var artist: string?
  var artworkData: string?
  var artworkDataType: string?
  var artworkUri: string?
  var compilation: string?
  var composer: string?
  var conductor: string?
  var description: string?
  var discNumber: number?
  var displayTitle: string?
  // var extras: Any?
  var genre: string?
  var isBrowsable: boolean?
  var isPlayable: boolean?
  var mediaType: string?
  var overallRating: number?
  var recordingDay: number?
  var recordingMonth: number?
  var recordingYear: number?
  var releaseDay: number?
  var releaseMonth: number?
  var releaseYear: number?
  var station: string?
  var subtitle: string?
  var title: string?
  var totalDiscCount: number?
  var totalTrackCount: number?
  var trackNumber: number?
  var userRating: number?
  var writer: string?
  /* List of custom fields derived from other fields. */
  var year: number?
}
