import MetadataRetriever from './MetadataRetriever';

import type {
  ConfigOptions,
  MediaMetadata,
  MediaMetadataExcerpt,
  MediaMetadataPublicField,
} from './constants';
import { MediaMetadataPublicFields, MetadataPresets } from './constants';

/** Returns the specified metadata of a media file from its uri. */
export function getMetadata<TOptions extends MediaMetadataPublicFields>(
  uri: string,
  options: TOptions
): Promise<MediaMetadataExcerpt<TOptions>> {
  return MetadataRetriever.getMetadata(uri, options) as Promise<
    MediaMetadataExcerpt<TOptions>
  >;
}

/**
 * Returns the artwork of the specified media file from its uri.
 * - Defaults to returning up to `5 MB` of data.
 */
export function getArtwork(uri: string): Promise<string | null> {
  return MetadataRetriever.getArtwork(uri);
}

export function updateConfigs(options: ConfigOptions): Promise<void> {
  return MetadataRetriever.updateConfigs(options);
}

export {
  type ConfigOptions,
  type MediaMetadata,
  type MediaMetadataExcerpt,
  type MediaMetadataPublicField,
  MediaMetadataPublicFields,
  MetadataPresets,
};
