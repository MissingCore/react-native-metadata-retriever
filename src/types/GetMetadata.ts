import type { MediaMetadata } from './MediaMetadata';
import type { MediaMetadataPublicFields } from './MediaMetadataPublicField';

import type { Prettify } from '../types.utils';

/** Returns a type-safe excerpt of `MediaMetadata`. */
export type MediaMetadataExcerpt<TKeys extends MediaMetadataPublicFields> =
  Prettify<Pick<MediaMetadata, TKeys[number]>>;

type ResultObject<TData> = { uri: string; data: TData };

export type BulkMetadata<TKeys extends MediaMetadataPublicFields> = {
  results: Array<ResultObject<MediaMetadataExcerpt<TKeys>>>;
  errors: Array<ResultObject<{ name: string; message: string }>>;
};
