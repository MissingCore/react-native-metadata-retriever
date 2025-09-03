# @missingcore/react-native-metadata-retriever

[<img src="https://img.shields.io/npm/v/@missingcore/react-native-metadata-retriever?style=for-the-badge&labelColor=000000" alt="NPM Version"/>](https://www.npmjs.com/package/@missingcore/react-native-metadata-retriever)
[<img src="https://img.shields.io/npm/l/@missingcore/react-native-metadata-retriever?style=for-the-badge&labelColor=000000" alt="License"/>](./LICENSE)

React Native wrapper for Android's unstable [`MetadataRetriever`](https://developer.android.com/reference/androidx/media3/exoplayer/MetadataRetriever) API, which fallback to the old [`MediaMetadataRetriever`](https://developer.android.com/reference/android/media/MediaMetadataRetriever) API if no metadata was found (ie: ID3v1 tags aren't detected).

## Supported Files

Unlike [`@missingcore/audio-metadata`](https://github.com/MissingCore/audio-metadata) which this is a successor to, this uses Android's native metadata reader via AndroidX's `MetadataRetriever` API. With that in mind, we can support a wider range of formats which would have costed a lot of time and energy to develop with pure TypeScript.

View the full list of supported audio formats on Android's documentation on [Supported media formats](https://developer.android.com/media/platform/supported-formats#audio-formats).

## Installation

```sh
npm install @missingcore/react-native-metadata-retriever
```

## Usage

```js
import {
  MetadataPresets,
  getArtwork,
  getMetadata,
} from '@missingcore/react-native-metadata-retriever';

const uri = 'file:///storage/emulated/0/Music/Silence.mp3';

// Of course with `await`, use this inside an async function or use `Promise.then()`.
const metadata = await getMetadata(uri, MetadataPresets.standard);
const base64Artwork = await getArtwork(uri);
```

## API Reference

## Constants

### MetadataPresets

```ts
const MetadataPresets: Record<string, MediaMetadataPublicFields>;
```

An object containing several metadata presets we can use to retrieve metadata.

### SaveFormat

```ts
const SaveFormat = {
  JPEG: 'jpeg',
  PNG: 'png',
  WEBP: 'webp',
};
```

Formats that we can save the image as.

## Functions

### getArtwork

```ts
function getArtwork(uri: string): Promise<string | null>;
```

Returns a base64 string representing the embedded artwork.

> **Note:** Defaults to returning up to `5 MB` of data. Can be configured with [`updateConfigs`](#updateConfigs).

### getBulkMetadata

```ts
function getBulkMetadata<TOptions extends MediaMetadataPublicFields>(
  uris: string[],
  options: TOptions
): Promise<BulkMetadata<TOptions>>;
```

Get the metadata of multiple uris.

### getMetadata

```ts
function getMetadata<TOptions extends MediaMetadataPublicFields>(
  uri: string,
  options: TOptions
): Promise<MediaMetadataExcerpt<TOptions>>;
```

Returns the specified metadata of the provided uri based on the `options` argument. Throws error if something went wrong.

> **Note:** The "complicated" typing is to make the resulting promise type-safe and be based off the provided `options`.

### saveArtwork

```ts
function saveArtwork(
  uri: string,
  options?: ArtworkOptions
): Promise<string | null>;
```

Returns the uri of the saved artwork.

> **Note:** Ignores the hard-limit of the max size of the image that can be saved.

### updateConfigs

```ts
function updateConfigs(options: ConfigOptions): Promise<void>;
```

Update internal configuration options such as the max size of the returned base64 image.

## Types

### ArtworkOptions

```ts
type ArtworkOptions = {
  /** A value in the range `0.0` - `1.0` specifying the compression level of the resulting image. */
  compress?: number;
  /** Specifies the format the image will be saved in. */
  format?: SaveFormat;
  /** Uri we want to save the artwork to instead of the cache directory. */
  saveUri?: string;
};
```

Options to change the behavior of `saveArtwork`.

### BulkMetadata

```ts
type BulkMetadata<TKeys extends MediaMetadataPublicFields> = {
  results: Array<{
    uri: string;
    data: MediaMetadataExcerpt<TKeys>;
  }>;
  errors: Array<{
    uri: string;
    data: { name: string; message: string };
  }>;
};
```

Structure returned when using `getBulkMetadata`.

### ConfigOptions

```ts
type ConfigOptions = {
  /**
   * Size of the returned base64 image in MB.
   * - Defaults to `5`.
   */
  maxImageSizeMB?: number | null;
};
```

Configuration options we can set to modify the behavior of the package.

### MediaMetadata

```ts
type MediaMetadata = {
  /* List of fields available on `Format`. */
  bitrate: number | null;
  channelCount: number | null;
  codecs: string | null;
  sampleMimeType: string | null;
  sampleRate: number | null; // in `Hz`
  /* List of fields available on `MediaMetadata`. */
  albumArtist: string | null;
  albumTitle: string | null;
  artist: string | null;
  artworkData: string | null;
  artworkDataType: string | null;
  artworkUri: string | null;
  compilation: string | null;
  composer: string | null;
  conductor: string | null;
  description: string | null;
  discNumber: number | null;
  displayTitle: string | null;
  // extras: unknown
  genre: string | null;
  isBrowsable: boolean | null;
  isPlayable: boolean | null;
  mediaType: string | null;
  overallRating: number | null;
  recordingDay: number | null;
  recordingMonth: number | null;
  recordingYear: number | null;
  releaseDay: number | null;
  releaseMonth: number | null;
  releaseYear: number | null;
  station: string | null;
  subtitle: string | null;
  title: string | null;
  totalDiscCount: number | null;
  totalTrackCount: number | null;
  trackNumber: number | null;
  userRating: number | null;
  writer: string | null;
  /* List of custom fields derived from other fields. */
  year: number | null;
};
```

The types of all the possible metadata we can return.

### MediaMetadataExcerpt

```ts
type MediaMetadataExcerpt<TKeys extends MediaMetadataPublicFields> = Prettify<
  Pick<MediaMetadata, TKeys[number]>
>;
```

Narrow down the returned types in `MediaMetadata` based on the `MediaMetadataPublicFields` provided.

## References

- [Android Support Library vs AndroidX](https://developer.android.com/jetpack/androidx)
- [AndroidX Media3 GitHub Repository](https://github.com/androidx/media)
- [Retrieving metadata](https://developer.android.com/media/media3/exoplayer/retrieving-metadata)
- [MetadataRetriever API](https://developer.android.com/reference/androidx/media3/exoplayer/MetadataRetriever)

## License

[MIT](./LICENSE)
