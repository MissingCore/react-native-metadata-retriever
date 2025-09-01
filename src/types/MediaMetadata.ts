/** Expected typed result of when we recieve metadata. */
export type MediaMetadata = {
  /* List of fields available on `Format`. */
  bitrate: number | null;
  channelCount: number | null;
  codecs: string | null;
  sampleMimeType: string | null;
  sampleRate: number | null; // in `Hz`
  /* List of fields available on `MediaMetadata`. */
  albumArtist: string | null;
  albumTitle: string | null;
  artist: string | null;
  artworkData: string | null;
  artworkDataType: string | null;
  artworkUri: string | null;
  compilation: string | null;
  composer: string | null;
  conductor: string | null;
  description: string | null;
  discNumber: number | null;
  displayTitle: string | null;
  // extras: unknown
  genre: string | null;
  isBrowsable: boolean | null;
  isPlayable: boolean | null;
  mediaType: string | null;
  overallRating: number | null;
  recordingDay: number | null;
  recordingMonth: number | null;
  recordingYear: number | null;
  releaseDay: number | null;
  releaseMonth: number | null;
  releaseYear: number | null;
  station: string | null;
  subtitle: string | null;
  title: string | null;
  totalDiscCount: number | null;
  totalTrackCount: number | null;
  trackNumber: number | null;
  userRating: number | null;
  writer: string | null;
  /* List of custom fields derived from other fields. */
  year: number | null;
};
