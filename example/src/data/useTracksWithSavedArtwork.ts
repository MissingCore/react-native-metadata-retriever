import {
  MetadataPresets,
  getBulkMetadata,
  saveArtwork,
} from '@missingcore/react-native-metadata-retriever';
import { useQuery } from '@tanstack/react-query';

import { getAudioFiles } from './getAudioFiles';

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

  const tracksMetadata: Array<
    (typeof results)['results'][number]['data'] & {
      id: string;
      filename: string;
      artworkData: string | null;
    }
  > = [];

  for (const { uri, data } of results.results) {
    try {
      const { id, filename } = assetURIMap[uri]!;
      const imgUri = await saveArtwork(uri, { compress: 0.8 });
      tracksMetadata.push({ id, filename, artworkData: imgUri, ...data });
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
