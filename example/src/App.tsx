import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import * as MediaLibrary from 'expo-media-library';
import { StatusBar } from 'expo-status-bar';
import { useEffect, useMemo, useState } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';

import { useTracksWithBase64Artwork } from './data/useTracksWithBase64Artwork';
import { useTracksWithSavedArtwork } from './data/useTracksWithSavedArtwork';

import { TrackList } from './components/TrackList';
import { Text } from './components/UI';

const queryClient = new QueryClient();

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
  const [showSaveMethod, setShowSaveMethod] = useState(false);

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

  const ShownList = useMemo(
    () => (showSaveMethod ? SavedList : Base64List),
    [showSaveMethod]
  );

  return (
    <>
      <Text variant="heading">
        Information about all the audio files
        `@missingcore/react-native-metadata-retriever` can identify.
      </Text>
      <View style={styles.buttonGroup}>
        <Pressable
          onPress={() => setShowSaveMethod(false)}
          style={[
            styles.button,
            !showSaveMethod ? styles.buttonActive : undefined,
          ]}
        >
          <Text>base64 Artwork</Text>
        </Pressable>
        <Pressable
          onPress={() => setShowSaveMethod(true)}
          style={[
            styles.button,
            showSaveMethod ? styles.buttonActive : undefined,
          ]}
        >
          <Text>Saved Artwork</Text>
        </Pressable>
      </View>

      <ShownList hasPermissions={hasPermissions} />
    </>
  );
}

function Base64List({ hasPermissions }: { hasPermissions: boolean }) {
  const queryResult = useTracksWithBase64Artwork(hasPermissions);
  return <TrackList hasPermissions={hasPermissions} {...queryResult} />;
}

function SavedList({ hasPermissions }: { hasPermissions: boolean }) {
  const queryResult = useTracksWithSavedArtwork(hasPermissions);
  return <TrackList hasPermissions={hasPermissions} {...queryResult} />;
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
    gap: 16,
    backgroundColor: '#ffffff',
  },
  buttonGroup: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 8,
  },
  button: {
    justifyContent: 'center',
    alignItems: 'center',
    padding: 12,
    backgroundColor: '#CCCCCC',
    borderRadius: 8,
  },
  buttonActive: {
    backgroundColor: '#FFD84D',
  },
});
