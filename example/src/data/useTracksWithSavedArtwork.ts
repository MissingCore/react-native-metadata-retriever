import {
  MetadataPresets,
  getBulkMetadata,
  saveHashedArtwork,
} from '@missingcore/react-native-metadata-retriever';
import { useQuery } from '@tanstack/react-query';

import { getAudioFiles } from './getAudioFiles';
import { ImageDirectory, getImageDirectory } from '../utils/fs';

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

  const savedHashedImages = new Set(
    getImageDirectory()
      .listAsRecords()
      .map(({ uri }) => uri.split('/').at(-1)?.split('.')[0])
      .filter((hash) => hash !== undefined)
  );

  const tracksMetadata: Array<
    (typeof results)['results'][number]['data'] & {
      id: string;
      filename: string;
      artworkData?: string | null;
    }
  > = [];

  for (const { uri, data } of results.results) {
    try {
      const { id, filename } = assetURIMap[uri]!;
      const img = await saveHashedArtwork(uri, {
        saveDirectory: ImageDirectory,
        knownHashes: Array.from(savedHashedImages),
        compress: 0.8,
      });
      if (img?.hash) savedHashedImages.add(img.hash);
      tracksMetadata.push({ id, filename, artworkData: img?.uri, ...data });
    } catch {}
  }

  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${(
      (performance.now() - start) /
      1000
    ).toFixed(4)}s.`
  );
  console.log('Errors:', results.errors);

  return {
    duration: ((performance.now() - start) / 1000).toFixed(4),
    tracks: tracksMetadata,
  };
}
