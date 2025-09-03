/**
 * Fields that can be extracted from media file.
 *
 * https://developer.android.com/reference/androidx/media3/common/MediaMetadata#public-fields_1.
 */
const MediaMetadataPublicFields = [
  /* List of fields available on `Format`. */
  'bitrate',
  'channelCount',
  'codecs',
  'sampleMimeType',
  'sampleRate',
  /* List of fields available on `MediaMetadata`. */
  'albumArtist',
  'albumTitle',
  'artist',
  'artworkData',
  'artworkDataType',
  'artworkUri',
  'compilation',
  'composer',
  'conductor',
  'description',
  'discNumber',
  'displayTitle',
  // 'extras',
  'genre',
  'isBrowsable',
  'isPlayable',
  'mediaType',
  'overallRating',
  'recordingDay',
  'recordingMonth',
  'recordingYear',
  'releaseDay',
  'releaseMonth',
  'releaseYear',
  'station',
  'subtitle',
  'title',
  'totalDiscCount',
  'totalTrackCount',
  'trackNumber',
  'userRating',
  'writer',
  /* List of custom fields derived from other fields. */
  'year',
] as const;

export type MediaMetadataPublicField =
  (typeof MediaMetadataPublicFields)[number];

export type MediaMetadataPublicFields = ReadonlyArray<MediaMetadataPublicField>;
