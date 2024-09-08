# @missingcore/react-native-metadata-retriever

[<img src="https://img.shields.io/npm/v/@missingcore/react-native-metadata-retriever?style=for-the-badge&labelColor=000000" alt="NPM Version"/>](https://www.npmjs.com/package/@missingcore/react-native-metadata-retriever)
[<img src="https://img.shields.io/npm/l/@missingcore/react-native-metadata-retriever?style=for-the-badge&labelColor=000000" alt="License"/>](./LICENSE)

React Native wrapper for Android's unstable [`MetadataRetriever`](https://developer.android.com/reference/androidx/media3/exoplayer/MetadataRetriever) API, which fallback to the old [`MediaMetadataRetriever`](https://developer.android.com/reference/android/media/MediaMetadataRetriever) API if no metadata was found (ie: ID3v1 tags aren't detected).

## Supported Files

Unlike [`@missingcore/audio-metadata`](https://github.com/MissingCore/audio-metadata) which this is a successor to, this uses Android's native metadata reader via AndroidX's `MetadataRetriever` API. With that in mind, we can support a wider range of formats which would have costed a lot of time and energy to develop with pure TypeScript.

View the full list of supported audio formats on Android's documentation on [Supported media formats](https://developer.android.com/media/platform/supported-formats#audio-formats).

> [!NOTE]  
> Since we're using AndroidX libraries and in addition, the unstable `MetadataRetriever` API, things may break in the future. Currently, we pinned the used AndroidX Media3 libraries to `1.4.1`, which should hopefully prevent any breaking changes.

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

### MediaMetadataPublicFields

```ts
const MediaMetadataPublicFields: string[];
```

An array containing the keys of supported metadata fields.

### MetadataPresets

```ts
const MetadataPresets: Record<string, MediaMetadataPublicField[]>;
```

An object containing several metadata presets we can use to retrieve metadata.

### MusicDirectoryPath

```ts
const MusicDirectoryPath: string;
```

Default path to the `Music` folder on device. This is usually `/storage/emulated/0/Music` or `/sdcard/Music` for older devices.

### PrimaryDirectoryPath

```ts
const PrimaryDirectoryPath: string;
```

Path to the primary shared/external storage directory. This is usually `/storage/emulated/0`.

### StorageVolumesDirectoryPaths

```ts
const StorageVolumesDirectoryPaths: string[];
```

An array of directory paths for all shared/external storage volumes. Includes attached external volumes such as SD cards and USB drives.

**Example output:** `["/storage/emulated/0"]`

## Functions

### getArtwork

```ts
function getArtwork(uri: string): Promise<string | null>;
```

Returns the base64 image string for the media file of the provided uri.

### getMetadata

```ts
function getMetadata<TOptions extends MediaMetadataPublicFields>(
  uri: string,
  options: TOptions
): Promise<MediaMetadataExcerpt<TOptions>>;
```

Returns the specified metadata of the provided uri based on the `options` argument.

**Note:** The "complicated" typing is to make the resulting promise type-safe and be based off the provided `options`.

## Types

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

### MediaMetadataPublicField

```ts
type MediaMetadataPublicField = (typeof MediaMetadataPublicFields)[number];
```

All the constant strings in the `MediaMetadataPublicFields` array as a type.

### MediaMetadataPublicFields

```ts
type MediaMetadataPublicFields = ReadonlyArray<MediaMetadataPublicField>;
```

`MediaMetadataPublicFields` array in the form of a type.

## References

- [Android Support Library vs AndroidX](https://developer.android.com/jetpack/androidx)
- [AndroidX Media3 GitHub Repository](https://github.com/androidx/media)
- [Retrieving metadata](https://developer.android.com/media/media3/exoplayer/retrieving-metadata)
- [MetadataRetriever API](https://developer.android.com/reference/androidx/media3/exoplayer/MetadataRetriever)

## License

[MIT](./LICENSE)
