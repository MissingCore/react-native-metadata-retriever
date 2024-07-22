import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): Promise<number>;

  getMetadata(uri: String, options: string[]): Promise<unknown>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
