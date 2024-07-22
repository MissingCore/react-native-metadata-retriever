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
  multiply,
  getMetadata,
} from '@missingcore/react-native-metadata-retriever';

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
    <View style={[styles.container, { paddingTop: insets.top + 64 }]}>
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

  const {} = useQuery({
    queryKey: ['tracks'],
    queryFn: testPackage,
    enabled: hasPermissions,
  });

  const [result, setResult] = useState<number | undefined>();

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

  useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
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

  await Promise.allSettled(audioFiles.map(({ uri }) => getMetadata(uri!, [])));

  console.log(
    `Got metadata of ${audioFiles.length} tracks in ${((performance.now() - start) / 1000).toFixed(4)}s.`
  );

  /*
    Quick Stats with Current Setup:
      - 186 tracks on OnePlus 6 took ~3-5s
      - 186 tracks on Nothing 2a took ~5.4-7s
  */

  return {};
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
