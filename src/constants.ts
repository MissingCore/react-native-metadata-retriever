import type { Prettify } from './utils';

/**
 * Fields that can be extracted from media file.
 *
 * https://developer.android.com/reference/androidx/media3/common/MediaMetadata#public-fields_1.
 */
export const MediaMetadataPublicFields = [
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

/** Some preset options that we can pass down into `getMetadata()`. */
export const MetadataPresets = {
  minimum: ['artist', 'title'],
  standard: [
    ...['artist', 'albumArtist', 'albumTitle', 'title'],
    ...['trackNumber', 'year'],
  ],
  standardArtwork: [
    ...['artist', 'albumArtist', 'albumTitle', 'title'],
    ...['trackNumber', 'year', 'artworkData'],
  ],
  statistics: [
    ...['bitrate', 'channelCount', 'codecs', 'sampleMimeType'],
    ...['sampleRate'],
  ],
} as const satisfies Record<string, MediaMetadataPublicField[]>;

export type MediaMetadataPublicField =
  (typeof MediaMetadataPublicFields)[number];

export type MediaMetadataPublicFields = ReadonlyArray<MediaMetadataPublicField>;

/** Expected typed result of when we recieve metadata. */
export type MediaMetadata = {
  /* List of fields available on `Format`. */
  bitrate: number | null;
  channelCount: number | null;
  codecs: string | null;
  sampleMimeType: string | null;
  sampleRate: number | null; // in `Hz`
  /* List of fields available on `MediaMetadata`. */
  albumArtist: string | null;
  albumTitle: string | null;
  artist: string | null;
  artworkData: string | null;
  artworkDataType: string | null;
  artworkUri: string | null;
  compilation: string | null;
  composer: string | null;
  conductor: string | null;
  description: string | null;
  discNumber: number | null;
  displayTitle: string | null;
  // extras: unknown
  genre: string | null;
  isBrowsable: boolean | null;
  isPlayable: boolean | null;
  mediaType: string | null;
  overallRating: number | null;
  recordingDay: number | null;
  recordingMonth: number | null;
  recordingYear: number | null;
  releaseDay: number | null;
  releaseMonth: number | null;
  releaseYear: number | null;
  station: string | null;
  subtitle: string | null;
  title: string | null;
  totalDiscCount: number | null;
  totalTrackCount: number | null;
  trackNumber: number | null;
  userRating: number | null;
  writer: string | null;
  /* List of custom fields derived from other fields. */
  year: number | null;
};

/** Returns a type-safe excerpt of `MediaMetadata`. */
export type MediaMetadataExcerpt<TKeys extends MediaMetadataPublicFields> =
  Prettify<Pick<MediaMetadata, TKeys[number]>>;
