import { FlashList } from '@shopify/flash-list';
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from '@tanstack/react-query';
import * as MediaLibrary from 'expo-media-library';
import { StatusBar } from 'expo-status-bar';
import { useState, useEffect } from 'react';
import { StyleSheet, View, Text } from 'react-native';
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

export default function RootLayout() {
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

function Container({ children }: { children: React.ReactNode }) {
  const insets = useSafeAreaInsets();
  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <StatusBar style="dark" />
      {children}
    </View>
  );
}

function App() {
  const [permissionResponse, requestPermission] = MediaLibrary.usePermissions({
    granularPermissions: ['audio'],
  });
  const [hasPermissions, setHasPermissions] = useState(false);

  const { isPending, error, data } = useQuery({
    queryKey: ['tracks'],
    queryFn: testPackage,
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

  return (
    <View style={styles.container}>
      <FlashList
        estimatedItemSize={23}
        data={data}
        keyExtractor={(_, index) => `${index}`}
        renderItem={({ item }) => <Text>{item.title}</Text>}
        ListEmptyComponent={
          isPending ? (
            <Text>Currently loading data...</Text>
          ) : error ? (
            <Text>{error.message}</Text>
          ) : (
            <Text>Failed to get metadata.</Text>
          )
        }
        contentContainerStyle={{ paddingVertical: 8 }}
      />
    </View>
  );
}

async function testPackage() {
  const start = performance.now();

  const { totalCount } = await MediaLibrary.getAssetsAsync({
    mediaType: 'audio',
    first: 0,
  });
  const audioFiles = (
    await MediaLibrary.getAssetsAsync({
      mediaType: 'audio',
      first: totalCount,
    })
  ).assets.filter(({ uri }) =>
    uri.startsWith('file:///storage/emulated/0/Music/')
  );

  const results = await Promise.allSettled(
    audioFiles.map(({ uri }) => getMetadata(uri, MetadataPresets.standard))
  );

  const tracksMetadata = results.filter(isFulfilled).map(({ value }) => value);
  const errors = results.filter(isRejected).map(({ reason }) => reason);

  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );
  console.log(errors);

  /*
    Quick Stats with Current Setup:
      - 186 tracks on OnePlus 6 took ~3-5s
      - 186 tracks on Nothing 2a took ~5.4-7s
  */

  return tracksMetadata;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 8,
  },
});
