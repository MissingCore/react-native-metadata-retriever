import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

//#region Codegen Types
/*
  FIXME: It's better to have type definitions inside a dedicated module,
  but RN Codegen currently doesn't support it.
    - https://github.com/reactwg/react-native-new-architecture/discussions/91#discussioncomment-13377469
*/

/** Extra options for when using `getArtwork`. */
type ArtworkOptions = {
  /**
   * A value in the range `0.0` - `1.0` specifying the quality of the resulting image.
   * - Defaults to `1`.
   */
  compress?: number;
  /**
   * Specifies the format the image will be saved in.
   * - Defaults to `SaveFormat.JPEG`.
   */
  format?: 'jpeg' | 'png' | 'webp';
  /** Uri we want to save the artwork to instead of the cache directory. */
  saveUri?: string;
};

/** Extra options for when using `getHashedArtwork`. */
type HashedArtworkOptions = {
  /**
   * A value in the range `0.0` - `1.0` specifying the quality of the resulting image.
   * - Defaults to `1`.
   */
  compress?: number;
  /**
   * Specifies the format the image will be saved in.
   * - Defaults to `SaveFormat.JPEG`.
   */
  format?: 'jpeg' | 'png' | 'webp';
  /** An array of known MD5 hashes formatted as a 32-character hexadecimal string. */
  knownHashes: string[];
  /** Directory where we want to save the hashed image. The file name will be the hash. */
  saveDirectory: string;
};

/** Options that can be set to modify the behavior of the package. */
type ConfigOptions = {
  /**
   * Size of the returned base64 image in MB.
   * - Defaults to `5`.
   */
  maxImageSizeMB?: number | null;
};

type DebugInfo = {
  /**
   * Result of `Format.toString()`.
   *
   * @see https://developer.android.com/reference/androidx/media3/common/Format
   */
  format: string[];
  /**
   * Result of `Metadata.toString()`.
   *
   * @see https://developer.android.com/reference/androidx/media3/common/Metadata
   */
  metadata: string[];
};
//#endregion

export interface Spec extends TurboModule {
  getBulkMetadata(
    uris: string[],
    options: string[]
  ): Promise<Record<string, any>>;

  getArtwork(
    uri: string,
    options: ArtworkOptions & { base64?: boolean }
  ): Promise<string | null>;
  getHashedArtwork(
    uri: string,
    options: HashedArtworkOptions
  ): Promise<{ hash: string; uri: string } | null>;

  getLyric(uri: string): Promise<string | null>;

  getR128Gain(uri: string): Promise<number | null>;

  updateConfigs(options: ConfigOptions): Promise<void>;

  /**
   * @deprecated For debugging purposes. Returns an object containing
   * the stringified `Format` & `Metadata` associated with the file.
   */
  debugEmbeddedTags(uri: string): Promise<DebugInfo>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MetadataRetriever');
