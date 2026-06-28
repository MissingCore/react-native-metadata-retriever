import MetadataRetriever from './MetadataRetriever';

import { MetadataPresets } from './constants';

import type {
  ArtworkOptions,
  HashedArtworkOptions,
} from './types/ArtworkOptions';
import { SaveFormat } from './types/ArtworkOptions';
import type { ConfigOptions } from './types/ConfigOptions';
import type { BulkMetadata, MediaMetadataExcerpt } from './types/GetMetadata';
import type { MediaMetadata } from './types/MediaMetadata';
import type {
  MediaMetadataPublicField,
  MediaMetadataPublicFields,
} from './types/MediaMetadataPublicField';

//#region Get Metadata
/** Get the metadata from multiple uris. */
export async function getBulkMetadata<
  TOptions extends MediaMetadataPublicFields,
>(uris: string[], options: TOptions) {
  return MetadataRetriever.getBulkMetadata(
    uris,
    options as unknown as string[]
  ) as Promise<BulkMetadata<TOptions>>;
}

/** Returns the specified metadata of a media file from its uri. */
export async function getMetadata<TOptions extends MediaMetadataPublicFields>(
  uri: string,
  options: TOptions
): Promise<MediaMetadataExcerpt<TOptions>> {
  const result = (await MetadataRetriever.getBulkMetadata(
    [uri],
    options as unknown as string[]
  )) as BulkMetadata<TOptions>;
  if (result.errors.length) {
    const { message, name } = result.errors[0]!.data;
    const error = Error(message);
    error.name = name;
    throw error;
  }
  return result.results[0]!.data;
}
//#endregion

//#region Get Artwork
/**
 * Returns a base64 string representing the embedded artwork.
 * - Defaults to returning up to `5 MB` of data.
 */
export async function getArtwork(uri: string): Promise<string | null> {
  return MetadataRetriever.getArtwork(uri, { base64: true });
}

/**
 * Returns the uri of the saved artwork.
 * - Ignores the hard-limit on the max size of the image that can be saved.
 */
export async function saveArtwork(
  uri: string,
  options?: ArtworkOptions
): Promise<string | null> {
  return MetadataRetriever.getArtwork(uri, options ?? {});
}

/**
 * Returns the hash & uri of the saved artwork.
 * - Ignores the hard-limit on the max size of the image that can be saved.
 * - The hash is based off the raw ByteArray before any formatting.
 */
export async function saveHashedArtwork(
  uri: string,
  options: HashedArtworkOptions
): Promise<{ hash: string; uri: string } | null> {
  return MetadataRetriever.getHashedArtwork(uri, options);
}
//#endregion

//#region Get Lyric
/** Attempts to return the embedded lyrics. */
export async function getLyric(uri: string): Promise<string | null> {
  return MetadataRetriever.getLyric(uri);
}
//#endregion

//#region Replay Gain
/** Returns the replay gain for the track. */
export async function getR128Gain(uri: string): Promise<number | null> {
  return MetadataRetriever.getR128Gain(uri);
}
//#endregion

//#region Configuration
/** Update internal configuration options. */
export async function updateConfigs(options: ConfigOptions): Promise<void> {
  return MetadataRetriever.updateConfigs(options);
}
//#endregion

//#region Debug Helpers
/**
 * @deprecated For debugging purposes. Returns an object containing
 * the stringified `Format` & `Metadata` associated with the file.
 */
export async function debugEmbeddedTags(uri: string) {
  return MetadataRetriever.debugEmbeddedTags(uri);
}
//#endregion

export {
  type ArtworkOptions,
  type BulkMetadata,
  type ConfigOptions,
  type HashedArtworkOptions,
  type MediaMetadata,
  type MediaMetadataExcerpt,
  type MediaMetadataPublicField,
  MetadataPresets,
  SaveFormat,
};
