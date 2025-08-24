package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext

import android.media.MediaMetadataRetriever
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.exoplayer.MetadataRetriever

import com.cyanchill.missingcore.metadataretriever.utils.MediaMetadataUtils
import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils


/**
 * Returns a list of `Format` from an uri from a process involving `MetadataRetriever.retrieveMetadata()`.
 *
 * @throws ExecutionException If file was not found from uri.
 * @throws TrackGroupArrayException If no tracks were found in media provided by the uri.
 *
 * @see <a href="https://developer.android.com/media/media3/exoplayer/retrieving-metadata#wo-playback">Link</a>
 */
fun getFormatList(context: ReactApplicationContext, uri: String): List<Format> {
  // Get static metadata of media from its uri.
  // See https://developer.android.com/media/media3/exoplayer/retrieving-metadata#kotlin
  val mediaItem = MediaItem.fromUri(NormalizationUtils.getSafeUri(uri))
  // Media3 v1.8.0 deprecated `retrieveMetadata` and requires us to use the builder.
  val metadataRetrieverInstance = MetadataRetriever.Builder(context, mediaItem).build()
  val trackGroupArray = metadataRetrieverInstance.retrieveTrackGroups().get()
  if (trackGroupArray == null) throw TrackGroupArrayException()

  // Unwrap the containers returned by `MetadataRetriever.retrieveMetadata`, getting a list
  // of `Format` from audio `TrackGroup`.
  val formatList = mutableListOf<Format>()
  for (i in 0 until trackGroupArray.length) {
    val trackGroup = trackGroupArray[i]
    // Only look at the track group containing audio.
    if (trackGroup.type != C.TRACK_TYPE_AUDIO) continue
    for (j in 0 until trackGroup.length) {
      // By definition, a `TrackGroup` should have at least 1 `Format`.
      // SEE https://developer.android.com/reference/androidx/media3/common/TrackGroup#TrackGroup(androidx.media3.common.Format...)
      formatList.add(trackGroup.getFormat(j))
    }
  }

  return formatList
}

/** Returns a list of `Metadata` from `List<Format>`. */
fun getMetadataListFromFormatList(formatList: List<Format>): List<Metadata> {
  val metadataList = mutableListOf<Metadata>()
  formatList.forEach {
    it.metadata?.let { metadataList.add(it) }
  }
  return metadataList
}
