import { FlashList } from '@shopify/flash-list';
import { Image } from 'expo-image';
import { StyleSheet, View } from 'react-native';

import type { useTracksWithBase64Artwork } from '../data/useTracksWithBase64Artwork';

import { Text } from './UI';

type TrackListProps = ReturnType<typeof useTracksWithBase64Artwork> & {
  hasPermissions: boolean;
};

export function TrackList({ hasPermissions, ...result }: TrackListProps) {
  const { isPending, error, data } = result;

  if (isPending) {
    return <Text variant="heading">Loading tracks...</Text>;
  } else if (error) {
    return (
      <>
        <Text variant="heading">An error was encountered:</Text>
        <Text variant="center">{error.message}</Text>
      </>
    );
  } else if (!hasPermissions) {
    return (
      <Text variant="heading">
        Read permissions for media content was not granted.
      </Text>
    );
  }

  return (
    <>
      <Text variant="center">Task completed in {data.duration}s.</Text>
      <Text variant="center">Total Tracks Found: {data.tracks.length}</Text>

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

const styles = StyleSheet.create({
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
});
