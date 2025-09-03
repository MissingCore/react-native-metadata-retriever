import type { ObjectValues } from 'src/types.utils';

export const SaveFormat = {
  JPEG: 'jpeg',
  PNG: 'png',
  WEBP: 'webp',
} as const;

export type SaveFormat = ObjectValues<typeof SaveFormat>;

/** Extra options for when using `getArtwork`. */
export type ArtworkOptions = {
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
  /** Uri we want to save the artwork to instead of the cache directory. */
  saveUri?: string;
};
