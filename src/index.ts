import MetadataRetriever from './MetadataRetriever';

import type {
  BulkMetadata,
  ConfigOptions,
  MediaMetadata,
  MediaMetadataExcerpt,
  MediaMetadataPublicField,
} from './constants';
import { MediaMetadataPublicFields, MetadataPresets } from './constants';

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
 * Returns the artwork of the specified media file from its uri.
 * - Defaults to returning up to `5 MB` of data.
 */
export async function getArtwork(uri: string): Promise<string | null> {
  return MetadataRetriever.getArtwork(uri);
}

export async function updateConfigs(options: ConfigOptions): Promise<void> {
  return MetadataRetriever.updateConfigs(options);
}

export {
  type BulkMetadata,
  type ConfigOptions,
  type MediaMetadata,
  type MediaMetadataExcerpt,
  type MediaMetadataPublicField,
  MediaMetadataPublicFields,
  MetadataPresets,
};
