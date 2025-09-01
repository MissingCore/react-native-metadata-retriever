/** Options that can be set to modify the behavior of the package. */
export type ConfigOptions = {
  /**
   * Size of the returned base64 image in MB.
   * - Defaults to `5`.
   */
  maxImageSizeMB?: number | null;
};
