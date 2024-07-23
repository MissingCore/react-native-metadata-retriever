import type { Prettify } from './utils';

/**
 * Fields that can be extracted from media file.
 *
 * https://developer.android.com/reference/androidx/media3/common/MediaMetadata#public-fields_1.
 */
export const MediaMetadataPublicFields = [
  'albumArtist',
  'albumTitle',
  'artist',
  // 'artworkData',
  // 'artworkDataType',
  // 'artworkUri',
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
  // 'mediaType',
  // 'overallRating',
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
  // 'userRating',
  'writer',
] as const;

export type MediaMetadataPublicField =
  (typeof MediaMetadataPublicFields)[number];

export type MediaMetadataPublicFields = ReadonlyArray<MediaMetadataPublicField>;

/** Expected typed result of when we recieve metadata. */
export type MediaMetadata = {
  albumArtist: string | null;
  albumTitle: string | null;
  artist: string | null;
  // artworkData: number[] | null;
  // artworkDataType: number | null;
  // artworkUri: string | null;
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
  // mediaType: number | null;
  // overallRating: number | null;
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
  // userRating: number | null;
  writer: string | null;
};

/** Returns a type-safe excerpt of `MediaMetadata`. */
export type MediaMetadataExcerpt<TKeys extends MediaMetadataPublicFields> =
  Prettify<Pick<MediaMetadata, TKeys[number]>>;
