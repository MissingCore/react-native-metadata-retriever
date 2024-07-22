import { NativeModules, Platform } from 'react-native';

import type { Spec } from './NativeMetadataRetriever';

const LINKING_ERROR =
  `The package '@missingcore/react-native-metadata-retriever' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({
    ios: '- This package does not support iOS.\n',
    default: '',
  }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const MetadataRetrieverModule = isTurboModuleEnabled
  ? require('./NativeMetadataRetriever').default
  : NativeModules.MetadataRetriever;

const MetadataRetriever = MetadataRetrieverModule
  ? MetadataRetrieverModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export default MetadataRetriever as Spec;
