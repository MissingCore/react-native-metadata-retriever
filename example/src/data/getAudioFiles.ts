import type { Asset, AssetMetadata } from 'expo-media-library';
import { AssetField, MediaType, Query } from 'expo-media-library';

const BATCH_AMOUNT = 500;

export async function getAudioFiles() {
  //? Get the `id` & `uri` of asset as `Query.exeForMetadata()` is missing
  //? the `uri` field.
  let foundAssets: Asset[] = [];
  let offset = 0;
  do {
    const results = await new Query()
      .eq(AssetField.MEDIA_TYPE, MediaType.AUDIO)
      .limit(BATCH_AMOUNT)
      .offset(offset)
      .exe();
    foundAssets = foundAssets.concat(results);
    offset += results.length;
    if (results.length !== BATCH_AMOUNT) break;
  } while (true);

  const assetMap: Record<string, { uri: string; metadata?: AssetMetadata }> =
    {};
  for (const asset of foundAssets) {
    assetMap[asset.id] = { uri: await asset.getUri() };
  }

  //? Get the metadata itself.
  let foundAssetMetadata: AssetMetadata[] = [];
  offset = 0;
  do {
    const results = await new Query()
      .eq(AssetField.MEDIA_TYPE, MediaType.AUDIO)
      .limit(BATCH_AMOUNT)
      .offset(offset)
      .exeForMetadata();
    foundAssetMetadata = foundAssetMetadata.concat(results);
    offset += results.length;
    if (results.length !== BATCH_AMOUNT) break;
  } while (true);

  for (const assetMetadata of foundAssetMetadata) {
    if (assetMap[assetMetadata.id])
      assetMap[assetMetadata.id]!.metadata = assetMetadata;
  }

  //? Convert back to array and filter out wanted entries.
  const formattedAssets = Object.values(assetMap)
    .filter((a) => a.metadata !== undefined)
    .map((a) => ({ uri: a.uri, ...a.metadata! }));

  // Limit media to those in the `Music` folder on our device.
  const audioFiles = formattedAssets.filter((a) =>
    a.uri.startsWith('file:///storage/emulated/0/Music/')
  );

  return audioFiles;
}
