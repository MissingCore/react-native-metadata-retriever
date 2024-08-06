# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project attempts to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[unreleased]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.1...HEAD
[0.2.1]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/MissingCore/react-native-metadata-retriever/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/MissingCore/react-native-metadata-retriever/releases/tag/v0.1.0
