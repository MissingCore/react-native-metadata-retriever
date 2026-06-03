package com.cyanchill.missingcore.metadataretriever.utils

import androidx.annotation.OptIn
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.metadata.vorbis.VorbisComment
import androidx.media3.extractor.mp3.Mp3InfoReplayGain

@OptIn(UnstableApi::class)
class ReplayGainParser(entry: Metadata.Entry) {
  var gain: Float? = null

  init {
    // Extract track gain based on the class.
    gain = when (entry) {
      is TextInformationFrame -> handleTextInformationFrame(entry)
      is Mp3InfoReplayGain -> handleMp3InfoReplayGain(entry)
      is VorbisComment -> handleVorbisComment(entry)
      else -> null
    }
  }

  private fun handleTextInformationFrame(frame: TextInformationFrame): Float? =
    when (frame.description?.uppercase()) {
      in COMMON_REPLAY_GAIN_TAGS -> frame.values[0].parseReplayGainAdjustment()
      else -> null
    }

  /** For when we see `ReplayGain Xing/Info`. */
  private fun handleMp3InfoReplayGain(frame: Mp3InfoReplayGain): Float? {
    return frame.field1?.gain
  }

  private fun handleVorbisComment(comment: VorbisComment): Float? =
    when (comment.key.uppercase()) {
      in COMMON_REPLAY_GAIN_TAGS -> comment.value.parseReplayGainAdjustment()
      else -> null
    }

  /** Some replay gain tags include "dB" in the string. */
  private fun String.parseReplayGainAdjustment() =
    replace(Regex("[^\\d.-]"), "").toFloatOrNull()

  companion object {
    private val COMMON_REPLAY_GAIN_TAGS = listOf("REPLAYGAIN_TRACK_GAIN", "R128_TRACK_GAIN")
  }
}
