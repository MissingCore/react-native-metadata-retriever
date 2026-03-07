import * as MediaLibrary from 'expo-media-library';

export async function getAudioFiles() {
  const { totalCount } = await MediaLibrary.getAssetsAsync({
    mediaType: 'audio',
    first: 0,
  });

  // Limit media to those in the `Music` folder on our device.
  const audioFiles = (
    await MediaLibrary.getAssetsAsync({
      mediaType: 'audio',
      first: totalCount,
    })
  ).assets.filter((a) => a.uri.startsWith('file:///storage/emulated/0/Music/'));

  return audioFiles;
}
