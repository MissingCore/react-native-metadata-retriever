import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import { MediaMetadataPublicFields } from './constants';

export type MediaMetadataPublicField =
  (typeof MediaMetadataPublicFields)[number];

export interface Spec extends TurboModule {
  readonly getConstants: () => Record<string, unknown>;

  multiply(a: number, b: number): Promise<number>;

  getMetadata<TOptions extends MediaMetadataPublicField>(
    uri: string,
    options: TOptions[]
  ): Promise<Record<TOptions, string | null>>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
