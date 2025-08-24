package com.cyanchill.missingcore.metadataretriever.models

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi

import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils

@OptIn(UnstableApi::class)
data class NormalizedFormatMetadataItem(val format: Format) : FormatMetadata {
  override val bitrate = NormalizationUtils.fixNoValue(format.bitrate)
  override val channelCount = NormalizationUtils.fixNoValue(format.channelCount)
  override val codecs = format.codecs
  override val sampleMimeType = format.sampleMimeType
  override val sampleRate = NormalizationUtils.fixNoValue(format.sampleRate)
}
