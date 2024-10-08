import { FlashList } from '@shopify/flash-list';
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from '@tanstack/react-query';
import { Image } from 'expo-image';
import * as MediaLibrary from 'expo-media-library';
import { StatusBar } from 'expo-status-bar';
import { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';

import {
  MetadataPresets,
  getMetadata,
} from '@missingcore/react-native-metadata-retriever';

import { isFulfilled, isRejected } from './utils/promise';

const queryClient = new QueryClient();

async function getTracks() {
  const start = performance.now();

  const { totalCount } = await MediaLibrary.getAssetsAsync({
    mediaType: 'audio',
    first: 0,
  });
  // Limit media to those in the `Music` folder on our device.
  let audioFiles = (
    await MediaLibrary.getAssetsAsync({
      mediaType: 'audio',
      first: totalCount,
    })
  ).assets.filter((a) => a.uri.startsWith('file:///storage/emulated/0/Music/'));
  console.log(
    `Got list of audio files in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );

  const tracksMetadata = await Promise.allSettled(
    audioFiles.map(async ({ id, filename, uri }) => {
      const data = await getMetadata(uri, MetadataPresets.standardArtwork);
      return { id, filename, ...data };
    })
  );
  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );

  const errors = tracksMetadata.filter(isRejected).map(({ reason }) => reason);
  console.log('Errors:', errors);

  return {
    duration: ((performance.now() - start) / 1000).toFixed(4),
    tracks: tracksMetadata.filter(isFulfilled).map(({ value }) => value),
  };
}

export default function RootLayer() {
  return (
    <SafeAreaProvider>
      <QueryClientProvider client={queryClient}>
        <Container>
          <App />
        </Container>
      </QueryClientProvider>
    </SafeAreaProvider>
  );
}

export function App() {
  const [permissionResponse, requestPermission] = MediaLibrary.usePermissions({
    granularPermissions: ['audio'],
  });
  const [hasPermissions, setHasPermissions] = useState(false);

  const { isPending, error, data } = useQuery({
    queryKey: ['tracks'],
    queryFn: getTracks,
    enabled: hasPermissions,
  });

  useEffect(() => {
    async function checkPermissions() {
      if (permissionResponse?.status !== 'granted') {
        const { canAskAgain, status } = await requestPermission();
        if (canAskAgain || status === 'denied') return;
      } else {
        setHasPermissions(true);
      }
    }
    checkPermissions();
  }, [permissionResponse?.status, requestPermission]);

  if (isPending) {
    return <Text style={styles.heading}>Loading tracks...</Text>;
  } else if (error) {
    return (
      <>
        <Text style={styles.heading}>An error was encountered:</Text>
        <Text style={styles.text}>{error.message}</Text>
      </>
    );
  } else if (!hasPermissions) {
    return (
      <Text style={styles.heading}>
        Read permissions for media content was not granted.
      </Text>
    );
  }

  return (
    <>
      <Text style={styles.heading}>
        Information about all the audio files
        `@missingcore/react-native-metadata-retriever` can identify.
      </Text>
      <Text style={styles.text}>Task completed in {data.duration}s.</Text>
      <Text style={styles.text}>Total Tracks Found: {data.tracks.length}</Text>

      <FlashList
        estimatedItemSize={166}
        data={data.tracks}
        keyExtractor={({ id }) => id}
        renderItem={({ item }) => (
          <View style={styles.metadataContainer}>
            <View style={styles.image}>
              <Image
                source={item.artworkData}
                contentFit="cover"
                style={styles.image}
              />
            </View>
            <View style={styles.infoContainer}>
              <Text numberOfLines={1}>{item.filename}</Text>
              <Text numberOfLines={1}>{item.title}</Text>
              <Text numberOfLines={1}>{item.artist}</Text>
              {!!item.albumTitle && (
                <Text numberOfLines={1}>{item.albumTitle}</Text>
              )}
              <Text numberOfLines={1}>{item.albumArtist}</Text>
              {!!item.trackNumber && <Text>Track {item.trackNumber}</Text>}
              {!!item.year && <Text>({item.year})</Text>}
            </View>
          </View>
        )}
      />
    </>
  );
}

function Container({ children }: { children: React.ReactNode }) {
  const insets = useSafeAreaInsets();
  return (
    <View style={[styles.container, { paddingTop: insets.top + 64 }]}>
      <StatusBar style="dark" />
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    gap: 8,
    backgroundColor: '#ffffff',
  },
  heading: {
    marginHorizontal: 16,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  text: {
    marginHorizontal: 16,
    textAlign: 'center',
  },
  metadataContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
    padding: 8,
    margin: 8,
    borderRadius: 16,
    backgroundColor: '#ebebeb',
    elevation: 4,
  },
  image: {
    width: 150,
    height: 150,
    backgroundColor: '#bdbdbd',
    borderRadius: 12,
  },
  infoContainer: {
    flex: 1,
  },
  bold: {
    fontWeight: 'bold',
  },
});
