import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getMetadata(
    uri: string,
    options: readonly string[]
  ): Promise<Record<string, unknown>>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
