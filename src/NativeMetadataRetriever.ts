import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type {
  MediaMetadataExcerpt,
  MediaMetadataPublicFields,
} from './constants';

export interface Spec extends TurboModule {
  readonly getConstants: () => Record<string, unknown>;

  getMetadata<TOptions extends MediaMetadataPublicFields>(
    uri: string,
    options: TOptions
  ): Promise<MediaMetadataExcerpt<TOptions>>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
