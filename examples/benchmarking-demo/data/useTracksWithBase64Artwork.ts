import {
  MetadataPresets,
  getBulkMetadata,
  updateConfigs,
} from '@missingcore/react-native-metadata-retriever';
import { useQuery } from '@tanstack/react-query';

import { getAudioFiles } from './getAudioFiles';

export function useTracksWithBase64Artwork(hasPermissions: boolean) {
  return useQuery({
    queryKey: ['tracks', 'base64Artwork'],
    queryFn: getTracksWithBase64Artwork,
    enabled: hasPermissions,
    gcTime: Infinity,
    staleTime: Infinity,
  });
}

async function getTracksWithBase64Artwork() {
  await updateConfigs({ maxImageSizeMB: 0.5 });

  const start = performance.now();

  const audioFiles = await getAudioFiles();
  console.log(
    `Got list of audio files in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );

  const assetURIMap = Object.fromEntries(
    audioFiles.map((asset) => [asset.uri, asset])
  );

  const results = await getBulkMetadata(
    audioFiles.map(({ uri }) => uri),
    MetadataPresets.standardArtwork
  );
  const tracksMetadata = results.results.map(({ uri, data }) => {
    const { id, filename } = assetURIMap[uri]!;
    return { id, filename, ...data };
  });
  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );
  console.log('Errors:', results.errors);

  return {
    duration: ((performance.now() - start) / 1000).toFixed(4),
    tracks: tracksMetadata,
  };
}
