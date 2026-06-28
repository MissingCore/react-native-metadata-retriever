import type { ObjectValues } from '../types.utils';

export const SaveFormat = {
  JPEG: 'jpeg',
  PNG: 'png',
  WEBP: 'webp',
} as const;

export type SaveFormat = ObjectValues<typeof SaveFormat>;

type SharedArtworkOptions = {
  /**
   * A value in the range `0.0` - `1.0` specifying the quality of the resulting image.
   * - Defaults to `1`.
   */
  compress?: number;
  /**
   * Specifies the format the image will be saved in.
   * - Defaults to `SaveFormat.JPEG`.
   */
  format?: SaveFormat;
};

/** Extra options for when using `getArtwork`. */
export type ArtworkOptions = SharedArtworkOptions & {
  /** Uri we want to save the artwork to instead of the cache directory. */
  saveUri?: string;
};

/** Extra options for when using `getHashedArtwork`. */
export type HashedArtworkOptions = SharedArtworkOptions & {
  /** Directory where we want to save the hashed image. The file name will be the hash. */
  saveDirectory: string;
  /**
   * An array of known MD5 hashes formatted as a 32-character hexadecimal string
   * which are stored in `saveDirectory`.
   */
  knownHashes: string[];
};
