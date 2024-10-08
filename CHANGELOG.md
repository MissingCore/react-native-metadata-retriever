# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project attempts to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[unreleased]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.5.0...HEAD
[0.5.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.2...v0.3.0
[0.2.2]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/MissingCore/react-native-metadata-retriever/releases/tag/v0.1.0
