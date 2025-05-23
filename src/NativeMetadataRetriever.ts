import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  readonly getConstants: () => {
    /**
     * Path of primary storage volume on device.
     *
     * @example `"/storage/emulated/0"`
     * @deprecated
     */
    PrimaryDirectoryPath: string;
    /**
     * Array of directory paths for all shared/external storage volumes.
     *
     * @example `["/storage/emulated/0", "/storage/0A08-1F1A"]`
     * @see https://developer.android.com/reference/android/content/Context#getExternalFilesDirs(java.lang.String)
     * @deprecated
     */
    StorageVolumesDirectoryPaths: string[];
    /**
     * Default path to the `Music` folder on device.
     *
     * @example `/storage/emulated/0/Music`
     * @example `/sdcard/Music`
     * @deprecated
     */
    MusicDirectoryPath: string | null;
  };

  getMetadata(
    uri: string,
    options: readonly string[]
  ): Promise<Record<string, unknown>>;

  getArtwork(uri: string): Promise<string | null>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
