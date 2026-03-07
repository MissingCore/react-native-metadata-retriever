import {
  MetadataPresets,
  getBulkMetadata,
  saveArtwork,
} from '@missingcore/react-native-metadata-retriever';
import { useQuery } from '@tanstack/react-query';

import { getAudioFiles } from './getAudioFiles';

import { isFulfilled } from '../utils/promise';

export function useTracksWithSavedArtwork(hasPermissions: boolean) {
  return useQuery({
    queryKey: ['tracks', 'savedArtwork'],
    queryFn: getTracksWithSavedArtwork,
    enabled: hasPermissions,
    gcTime: Infinity,
    staleTime: Infinity,
  });
}

async function getTracksWithSavedArtwork() {
  const start = performance.now();

  const audioFiles = await getAudioFiles();
  console.log(
    `Got list of audio files in ${((performance.now() - start) / 1000).toFixed(
      4
    )}s.`
  );

  const assetURIMap = Object.fromEntries(
    audioFiles.map((asset) => [asset.uri, asset])
  );

  const results = await getBulkMetadata(
    audioFiles.map(({ uri }) => uri),
    MetadataPresets.standard
  );
  const tracksMetadata = await Promise.allSettled(
    results.results.map(async ({ uri, data }) => {
      const { id, filename } = assetURIMap[uri]!;
      const imgUri = await saveArtwork(uri, { compress: 0.8 });
      return { id, filename, artworkData: imgUri, ...data };
    })
  );
  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${(
      (performance.now() - start) /
      1000
    ).toFixed(4)}s.`
  );
  console.log('Errors:', results.errors);

  return {
    duration: ((performance.now() - start) / 1000).toFixed(4),
    tracks: tracksMetadata.filter(isFulfilled).map(({ value }) => value),
  };
}
