import MetadataRetriever from './MetadataRetriever';

import { MetadataPresets } from './constants';

import type { ArtworkOptions } from './types/ArtworkOptions';
import type { ConfigOptions } from './types/ConfigOptions';
import type { BulkMetadata, MediaMetadataExcerpt } from './types/GetMetadata';
import type { MediaMetadata } from './types/MediaMetadata';
import type {
  MediaMetadataPublicField,
  MediaMetadataPublicFields,
} from './types/MediaMetadataPublicField';

/** Returns the specified metadata of a media file from its uri. */
export async function getBulkMetadata<
  TOptions extends MediaMetadataPublicFields,
>(uris: string[], options: TOptions) {
  return MetadataRetriever.getBulkMetadata(uris, options);
}

/** Returns the specified metadata of a media file from its uri. */
export async function getMetadata<TOptions extends MediaMetadataPublicFields>(
  uri: string,
  options: TOptions
): Promise<MediaMetadataExcerpt<TOptions>> {
  const result = await MetadataRetriever.getBulkMetadata([uri], options);
  if (result.errors.length) {
    const { message, name } = result.errors[0]!.data;
    const error = Error(message);
    error.name = name;
    throw error;
  }
  return result.results[0]!.data;
}

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

export async function updateConfigs(options: ConfigOptions): Promise<void> {
  return MetadataRetriever.updateConfigs(options);
}

export {
  type ArtworkOptions,
  type BulkMetadata,
  type ConfigOptions,
  type MediaMetadata,
  type MediaMetadataExcerpt,
  type MediaMetadataPublicField,
  MetadataPresets,
};
