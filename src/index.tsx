import MetadataRetriever from './MetadataRetriever';

import type { MediaMetadataPublicField } from './NativeMetadataRetriever';

export * from './constants';

export function multiply(a: number, b: number): Promise<number> {
  return MetadataRetriever.multiply(a, b);
}

export function getMetadata<TOptions extends MediaMetadataPublicField>(
  uri: string,
  options: TOptions[]
): Promise<Record<TOptions, string | null>> {
  return MetadataRetriever.getMetadata(uri, options);
}

export { type MediaMetadataPublicField };
