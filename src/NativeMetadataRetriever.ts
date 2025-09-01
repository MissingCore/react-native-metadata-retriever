import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type { ConfigOptions } from './types/ConfigOptions';
import type { BulkMetadata } from './types/GetMetadata';
import type { MediaMetadataPublicFields } from './types/MediaMetadataPublicField';

export interface Spec extends TurboModule {
  getBulkMetadata<TOptions extends MediaMetadataPublicFields>(
    uris: string[],
    options: TOptions
  ): Promise<BulkMetadata<TOptions>>;

  getArtwork(uri: string): Promise<string | null>;

  updateConfigs(options: ConfigOptions): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
