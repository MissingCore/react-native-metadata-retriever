import MetadataRetriever from './MetadataRetriever';

import type {
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

/** Returns the artwork of specified media file from its uri. */
export function getArtwork(uri: string): Promise<string | null> {
  return MetadataRetriever.getArtwork(uri);
}

const {
  MusicDirectoryPath,
  PrimaryDirectoryPath,
  StorageVolumesDirectoryPaths,
} = MetadataRetriever.getConstants();

export {
  type MediaMetadata,
  type MediaMetadataExcerpt,
  type MediaMetadataPublicField,
  MediaMetadataPublicFields,
  MetadataPresets,
  // System paths.
  MusicDirectoryPath,
  PrimaryDirectoryPath,
  StorageVolumesDirectoryPaths,
};
