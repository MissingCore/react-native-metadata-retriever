import type { MediaMetadataPublicFields } from './types/MediaMetadataPublicField';

/** Some preset options that we can pass down into `getMetadata()`. */
export const MetadataPresets = {
  album: ['artist', 'albumArtist', 'albumTitle', 'year'],
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
} as const satisfies Record<string, MediaMetadataPublicFields>;
