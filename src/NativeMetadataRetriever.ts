import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  readonly getConstants: () => {
    /**
     * Default path to the `Music` folder on device.
     *
     * @example `/storage/emulated/0/Music`
     * @example `/sdcard/Music`
     */
    MusicDirectoryPath: string;
    /**
     * Path of primary storage volume on device.
     *
     * @example `"/storage/emulated/0"`
     */
    PrimaryDirectoryPath: string;
    /**
     * Array of directory paths for all shared/external storage volumes.
     *
     * @example `["/storage/emulated/0", "/storage/0A08-1F1A"]`
     * @see https://developer.android.com/reference/android/content/Context#getExternalFilesDirs(java.lang.String)
     */
    StorageVolumesDirectoryPaths: string[];
  };

  getMetadata(
    uri: string,
    options: readonly string[]
  ): Promise<Record<string, unknown>>;

  getArtwork(uri: string): Promise<string | null>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
