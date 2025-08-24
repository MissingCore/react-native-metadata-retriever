package com.cyanchill.missingcore.metadataretriever.models

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi

import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils

/**
 * Metadata fields found on `Format` that has been normalized for our use.
 *
 * @see <a href="https://developer.android.com/reference/androidx/media3/common/Format">Link</a>
 */
interface NormalizedFormatMetadata {
  val bitrate: Int?
  val channelCount: Int?
  val codecs: String?
  val sampleMimeType: String?
  val sampleRate: Int? // in `Hz`
}

@OptIn(UnstableApi::class)
data class NormalizedFormatMetadataItem(val format: Format): NormalizedFormatMetadata {
  override val bitrate = NormalizationUtils.fixNoValue(format.bitrate)
  override val channelCount = NormalizationUtils.fixNoValue(format.channelCount)
  override val codecs = format.codecs
  override val sampleMimeType = format.sampleMimeType
  override val sampleRate = NormalizationUtils.fixNoValue(format.sampleRate)
}
