# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project attempts to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Getting metadata supported by `MediaMetadata` API & a subset from the `Format` API.
- Getting image of media from its uri as a base64 string.
- Add fallback to `MediaMetadataRetriever` if we failed to get metadata with `MetadataRetriever` (ie: ID3v1 metadata)
- Ensure compatibility with both new & old architecture.

## [0.0.0] - 2024-07-20

Add section to make `release-it` not complain that this is missing. 2024-07-20 is when we really started working on this repository.
