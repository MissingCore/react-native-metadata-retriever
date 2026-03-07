# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project attempts to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### ⚙️ Internal Changes

- Rewrite app to use Turbo Native Modules.

## [2.5.0] - 2026-02-04

### 🎉 Added

- Support `x/y` format for `discNumber` for FLAC (via https://github.com/MissingCore/media/commit/56c92dc9e1e99e5083e0cdb314636d3343eda76c).

### ⚡ Changes

- `x/y` format for `discNumber` & `trackNumber` now also populate `totalDiscCount` & `totalTrackCount` for FLAC.

### ⚙️ Internal Changes

- Improve reliability of reading JitPack dependency by using `https://jitpack.io` instead of `https://www.jitpack.io`.

## [2.4.0] - 2026-01-29

### ⚡ Changes

- Bumped AndroidX media3 to `1.9.1` from `1.8.0`.
- Update import of `MetadataRetriever` to new `:media3-inspector` module.

## [2.3.0] - 2026-01-23

### 🎉 Added

- Add support for `discNumber` & `totalDiscCount` fields for ID3 (via https://github.com/MissingCore/media/commit/2ef44ffa0f8d0789f80bd17914734a65d955591c).
  - Supports `x` & `x/y` formats.

## [2.2.1] - 2025-12-03

### ⚙️ Internal Changes

- Update to PNPM 10.
- Upgrade example app to Expo SDK 54 / RN 0.81.

## [2.2.0] - 2025-10-21

### 🎉 Added

- Add support for `recordingDay`, `recordingMonth`, `recordingYear` fields for `.flac` files.
  - I don't think this was supported with `MediaMetadataRetriever` from looking at the [ExoPlayer2 code](https://github.com/google/ExoPlayer/blob/release-v2/library/extractor/src/main/java/com/google/android/exoplayer2/metadata/flac/VorbisComment.java).

## [2.2.0-beta.1] - 2025-10-21

### 🛠️ Fixes

- `Other` artwork classification not being saved.
  - Encountered a `.flac` file whose artwork was classified as `Other` but had `artworkDataType = -1`.
- Get `trackNumber` from `.flac` files formatted as `x/x` (previously returned `null`).

### ⚙️ Internal Changes

- Switch to using [our fork of `androidx/media`](https://github.com/MissingCore/media).
  - This allows us to patch in some fixes.

## [2.1.0] - 2025-09-29

### 🛠️ Fixes

- Build issues for New Architecture (CodeGen doesn't support imported types).

### ⚙️ Internal Changes

- Enable New Architecture in example app.

## [2.0.0] - 2025-09-26

### ⚙️ Internal Changes

- Flatten `examples/*` to single `example` folder.
- Use `BaseReactPackage` instead of deprecated `TurboReactPackage`.

## [2.0.0-beta.2] - 2025-09-02

### ❗ Breaking Changes

- Revised "compression" option for `saveArtwork`.
- Changed `ArtworkOptions` type.
  - `compress` is now a number between `0.0` & `1.0` specifying the quality of the saved image (defaults to `1`).
  - New `format` option specifying the file format the image will be saved as (defaults to `jpeg`).

## [2.0.0-beta.1] - 2025-09-02

### ❗ Breaking Changes

- Remove additional fallback when getting the `year` metadata field for `MetadataRetriever` method.
  - No longer fallbacks to using `MediaMetadataRetriever` if everything else fails.
- Remove `MediaMetadataPublicFields` array & type export.

### 🎉 Added

- New `updateConfigs` function.
  - Customize the max size of the returned base64 image with the `maxImageSizeMB` option.
- New `getBulkMetadata` function.
  - Slightly different than `getMetadata` as this function won't throw an error. It'll return an object with 2 fields, `results` & `errors` which contain an array of objects containing the URI & data/error associated with it.
- New `saveArtwork` function.
  - Allows the user to save the embedded artwork to a file and optionally compress it to save on space.

### ⚙️ Internal Changes

- Reorganization of code.
  - Now "normalize" and put all the field-value pairs inside a map, which we then send the ones we want to return.
- Ensure we release `MetadataRetriever` & `MediaMetadataRetriever` when we're done using them.
- Updated example app to use `getBulkMetadata` & `saveArtwork` instead of `getMetadata`.

## [1.1.0] - 2025-08-21

### ⚡ Changes

- Bumped AndroidX media3 to `1.8.0` from `1.6.1`.

### Other

- Bumped dependencies.

## [1.0.0] - 2025-07-06

### ❗ Breaking Changes

- Removed deprecated `PrimaryDirectoryPath`, `StorageVolumesDirectoryPaths`, and `MusicDirectoryPath`.
  - It seems like `getTypedExportedConstants()`, which creates these constants caused crashes on some devices according to the Google Play Console.

### Other

- Bumped dependencies.

## [0.9.0] - 2025-05-24

### ⚡ Changes

- Bumped AndroidX media3 to `1.6.1` from `1.5.1`.
- Mark `PrimaryDirectoryPath` & `StorageVolumesDirectoryPaths` as deprecated.
  - This library shouldn't care about where the media comes from and the fact that `StorageVolumesDirectoryPaths` might not return everything.
  - Will be removed in `v1.0.0`.

### 🛠️ Fixes

- Fixed type errors breaking React Native 0.77 builds caused by upgrading Kotlin to v2.0 from v1.9.

### Other

- Validate that the New Architecture works in the example app.
  - Required a workaround for CMAKE due to long paths in Windows.
- Validate compatibility with React Native 0.79 & Expo SDK 53.

## [0.8.0] - 2025-02-13

### ⚡ Changes

- We now also consider saving covers classified as `"32x32 pixels 'file icon' (PNG only)"` when using `getArtwork()`.
  - The order of priority is: `"Cover (front)"` → `"Other"` → `"32x32 pixels 'file icon' (PNG only)"`.

## [0.7.5] - 2025-02-13

### ⚡ Changes

- Ensure we don't return `trackNumber = 0` when getting the metadata using `MetadataRetriever`.
  - This matches the behavior we do with `MediaMetadataRetriever`.

## [0.7.4] - 2025-02-10

### 🛠️ Fixes

- Error thrown when trying to read metadata from a file that contains a `%` in its filename.
  - If we had `%20` in the filename, it'll get incorrectly decoded to a space (` `), resulting in the file not being found.

## [0.7.3] - 2025-02-06

### ⚡ Changes

- Bumped AndroidX media3 to `1.5.1` from `1.5.0`.
- Release `MediaMetadataRetriever` resources in `getMetadata()` after we're done with it.

### 🛠️ Fixes

- Issue where `MetadataRetriever` doesn't find the disc & track number for `.flac` files.
- Issue where `MediaMetadataRetriever` incorrectly parses some characters from `.flac` files.

## [0.7.2] - 2025-01-31

### 🛠️ Fixes

- Parsing issue with `year` metadata field if we have an invalid value that's less than 4 characters long.
- Error thrown when trying to read metadata from a file that contains a `?` or `#` in its filename.

## [0.7.1] - 2024-12-10

### ⚡ Changes

- Add fallback to `PrimaryDirectoryPath` & `StorageVolumesDirectoryPaths` to be `"/storage/emulated/0"` & `["/storage/emulated/0"]` respectively.
- Mark `MusicDirectoryPath` as deprecated mainly due to not specifying a fallback (ie: it may be `null`).
  - Will be removed in `v1.0.0`.

### 🛠️ Fixes

- Attempt to fix `stacktrace: com.facebook.react.common.JavascriptException: Error: Exception in HostObject::get for prop 'MetadataRetriever': java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.io.File.getAbsolutePath()' on a null object reference` that occurs on some devices.
  - We don't have a device to replicate this issue so we're kind of going in blind.
- Fallback to using `MediaMetadataRetriever` if bitrate isn't found or if the value isn't "correct".
  - Bitrate isn't present in `Format` for `.flac` files and I've seen variable bitrate for `.mp3` returned as `64000`, which is incorrect.
  - The reason for these issues may be related to [this issue comment](https://github.com/androidx/media/issues/1081#issuecomment-1936301349).

## [0.7.0] - 2024-12-07

> [!NOTE]
> Use `v0.6.0` if you don't want the previous `targetSdkVersion` or `compileSdkVersion` to change.

### ⚡ Changes

- Bumped AndroidX media3 to `1.5.0` from `1.4.1`.
  - Requires setting `compileSdkVersion=35` & patching `expo-modules-core` due to Kotlin type errors.

### Other

- Validate compatibility with React Native 0.76.
  - A patch file was required to get the example working due to monorepo behaviors.

## [0.6.2] - 2025-01-31

### 🛠️ Fixes

- Parsing issue with `year` metadata field if we have an invalid value that's less than 4 characters long.
- Error thrown when trying to read metadata from a file that contains a `?` or `#` in its filename.

## [0.6.1] - 2024-12-10

### ⚡ Changes

- Add fallback to `PrimaryDirectoryPath` & `StorageVolumesDirectoryPaths` to be `"/storage/emulated/0"` & `["/storage/emulated/0"]` respectively.
- Mark `MusicDirectoryPath` as deprecated mainly due to not specifying a fallback (ie: it may be `null`).
  - Will be removed in `v1.0.0`.

### 🛠️ Fixes

- Attempt to fix `stacktrace: com.facebook.react.common.JavascriptException: Error: Exception in HostObject::get for prop 'MetadataRetriever': java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.io.File.getAbsolutePath()' on a null object reference` that occurs on some devices.
  - We don't have a device to replicate this issue so we're kind of going in blind.
- Fallback to using `MediaMetadataRetriever` if bitrate isn't found or if the value isn't "correct".
  - Bitrate isn't present in `Format` for `.flac` files and I've seen variable bitrate for `.mp3` returned as `64000`, which is incorrect.
  - The reason for these issues may be related to [this issue comment](https://github.com/androidx/media/issues/1081#issuecomment-1936301349).

## [0.6.0] - 2024-12-07

> [!NOTE]
> This will be followed by `v0.7.0` which will update AndroidX media3 to `1.5.0`, which requires setting the compiled SDK version to 35.

### ⚡ Changes

- Set max cap of size of base64 image returned from `getArtwork()` to `5MB` (this means capping the image found to `3.75MB` due to converting a byte array to a base64 string increasing the size by 33-37%).
  - This should further reduce the risk of `OutOfMemoryError` and will catch extreme cases such as trying to convert massive byte arrays (ie: >50MB) to a base64 string.

### 🛠️ Fixes

- Workaround with supporting other metadata fields in `.flac` files by utilizing `MediaMetadataRetriever`.
  - Someone noticed that track numbers were missing with `.flac` files. Upon further investigation, we've also seen that the `discNumber` value was also missing. This was not the case when using `MediaMetadataRetriever`.
  - Will like to implement a better fix in the future.

### Other

- `EXPO_UNSTABLE_CORE_AUTOLINKING=1` (mentioned in [0.3.0]) is no longer needed in the `.env` file for the Expo example with `react-native@0.75.4`.
  - Narrowed down to this causing crashing in the production app and the development version showing a white screen.

## [0.5.0] - 2024-09-21

### ⚡ Changes

- Switched to `String.toIntOrNull()` to prevent `NumberFormatException` caused by incorrect metadata structure.
- `getArtwork` only attempts to read the 1st "Other" picture type (instead of overriding previous values if they existed).
- Updated how we rejected promises.
  - I wonder if the cause of the `message` field of the error received in React being `null` is due to the use of the following `promise.reject` format: `promise.reject(code: String, throwable: Throwable?)`

### 🛠️ Fixes

- Fixed `No suitable media source factory found for content type: 2` error due to missing modules to support the `HLS` stream.
  - Added modules for `DASH`, `SmoothStreaming`, and `RTSP` as well.

## [0.4.0] - 2024-09-08

### ⚡ Changes

- Bumped AndroidX media3 to `1.4.1` from `1.3.1`.

### 🛠️ Fixes

- Ensure "year" field returns a year.
- Reduce risk of `OutOfMemoryError` due to trying to convert a large byte array representing an image into a base64 string.

## [0.3.0] - 2024-09-01

### ⚡ Changes

- No longer provide `Unknown ExecutionException` & `Metadata Retrieval Error` message with promise rejection for unknown errors (ie: now default to the message in the error object).
- Support React Native 0.75.
  - For the Expo example to work (as I've encountered `ERROR: autolinkLibrariesFromCommand: process cmd /c npx @react-native-community/cli config exited with error code: 1`), you need `EXPO_UNSTABLE_CORE_AUTOLINKING=1` in your `.env` file. Not sure if this is needed in a production app.

### 🛠️ Fixes

- Issue where an app using this package crashes in Android 7 to 10. This was due to the `StorageVolume.getDirectory()` being introduced in Android 11.

### 📚 Documentation

- Document `PrimaryDirectoryPath` in `README.md`.

## [0.2.2] - 2024-08-06

### Added

- New `PrimaryDirectoryPath` exported variable.

## [0.2.1] - 2024-07-26

### Added

- New custom `year` metadata field that returns: `recordingYear` -> `releaseYear` -> year parsed from `MediaMetadataRetriever`'s `DATE` field.
- New `album` metadata preset.

## [0.2.0] - 2024-07-26

### Changed

- Changed package id to `com.cyanchill.missingcore.metadataretriever` from `com.missingcore.metadataretriever` (ie: to a domain we control).

## [0.1.0] - 2024-07-25

### Added

- Getting metadata supported by `MediaMetadata` API & a subset from the `Format` API.
- Getting image of media from its uri as a base64 string.
- Add fallback to `MediaMetadataRetriever` if we failed to get metadata with `MetadataRetriever` (ie: ID3v1 metadata)
- Ensure compatibility with both new & old architecture.

## [0.0.0] - 2024-07-20

Add section to make `release-it` not complain that this is missing. 2024-07-20 is when we really started working on this repository.

[unreleased]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.5.0...HEAD
[2.5.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.4.0...v2.5.0
[2.4.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.3.0...v2.4.0
[2.3.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.2.1...v2.3.0
[2.2.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.2.0...v2.2.1
[2.2.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.2.0-beta.1...v2.2.0
[2.2.0-beta.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.1.0...v2.2.0-beta.1
[2.1.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.0.0-beta.2...v2.0.0
[2.0.0-beta.2]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v2.0.0-beta.1...v2.0.0-beta.2
[2.0.0-beta.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v1.1.0...v2.0.0-beta.1
[1.1.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.9.0...v1.0.0
[0.9.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.8.0...v0.9.0
[0.8.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.5...v0.8.0
[0.7.5]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.4...v0.7.5
[0.7.4]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.3...v0.7.4
[0.7.3]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.2...v0.7.3
[0.7.2]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.1...v0.7.2
[0.7.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.6.0...v0.7.0
[0.6.2]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.6.1...v0.6.2
[0.6.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.6.0...v0.6.1
[0.6.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.2...v0.3.0
[0.2.2]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/MissingCore/react-native-metadata-retriever/releases/tag/v0.1.0
