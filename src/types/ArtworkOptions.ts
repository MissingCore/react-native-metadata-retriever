/** Extra options for when using `getArtwork`. */
export type ArtworkOptions = {
  /** Uri we want to save the artwork to instead of the cache directory. */
  saveUri?: string;
  /** Whether we should compress the saved image to 80% image quality. */
  compress?: boolean;
};
