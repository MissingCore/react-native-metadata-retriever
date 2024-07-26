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
     * Array of directory paths for all shared/external storage volumes.
     * Includes attached external volumes such as SD cards and USB drives.
     *
     * @example `["/storage/emulated/0"]`
     * @see https://developer.android.com/reference/android/os/storage/StorageManager#getStorageVolumes()
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
