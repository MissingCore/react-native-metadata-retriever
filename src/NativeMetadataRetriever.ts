import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type {
  BulkMetadata,
  ConfigOptions,
  MediaMetadataPublicFields,
} from './constants';

export interface Spec extends TurboModule {
  getBulkMetadata<TOptions extends MediaMetadataPublicFields>(
    uris: string[],
    options: TOptions
  ): Promise<BulkMetadata<TOptions>>;

  getArtwork(uri: string): Promise<string | null>;

  updateConfigs(options: ConfigOptions): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
