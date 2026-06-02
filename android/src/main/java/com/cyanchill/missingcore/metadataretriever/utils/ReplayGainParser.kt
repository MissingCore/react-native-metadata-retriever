package com.cyanchill.missingcore.metadataretriever.utils

import androidx.annotation.OptIn
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.mp3.Mp3InfoReplayGain

@OptIn(UnstableApi::class)
class ReplayGainParser(entry: Metadata.Entry) {
  var gain: Float? = null

  init {
    // Extract track gain based on the class.
    gain = when (entry) {
      is TextInformationFrame -> handleTextInformationFrame(entry)
      is Mp3InfoReplayGain -> handleMp3InfoReplayGain(entry)
      else -> null
    }
  }

  private fun handleTextInformationFrame(frame: TextInformationFrame): Float? =
    when (frame.description?.uppercase()) {
      "REPLAYGAIN_TRACK_GAIN" -> frame.values[0].parseReplayGainAdjustment()
      else -> null
    }

  /** For when we see `ReplayGain Xing/Info`. */
  private fun handleMp3InfoReplayGain(frame: Mp3InfoReplayGain): Float? {
    return frame.field1?.gain
  }

  /** Some replay gain tags include "dB" in the string. */
  private fun String.parseReplayGainAdjustment() =
    replace(Regex("[^\\d.-]"), "").toFloatOrNull()
}
