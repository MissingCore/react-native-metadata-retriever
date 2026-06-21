package com.cyanchill.missingcore.metadataretriever.modules

import androidx.annotation.OptIn
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.metadata.vorbis.VorbisComment
import androidx.media3.extractor.mp3.Mp3InfoReplayGain

@OptIn(UnstableApi::class)
class ReplayGainParser(metadataList: List<Metadata>) {
  var gain: Float? = null

  init {
    for (metadata in metadataList) {
      val numEntries = metadata.length()
      for (i in 0 until numEntries) {
        parseMetadataEntry(metadata[i])
        if (gain != null) break
      }
      if (gain != null) break
    }
  }

  private fun parseMetadataEntry(entry: Metadata.Entry) {
    gain = when (entry) {
      is TextInformationFrame -> handleTextInformationFrame(entry)
      is Mp3InfoReplayGain -> handleMp3InfoReplayGain(entry)
      is VorbisComment -> handleVorbisComment(entry)
      else -> null
    }
  }

  //#region [ReplayGain Containers]
  private fun handleTextInformationFrame(frame: TextInformationFrame): Float? =
    when (frame.description?.uppercase()) {
      "REPLAYGAIN_TRACK_GAIN" -> frame.values.firstOrNull()?.parseReplayGainAdjustment()
      //? R128 gain needs to be divided by `256f`.
      "R128_TRACK_GAIN" -> frame.values.firstOrNull()?.parseReplayGainAdjustment()?.div(256f)
      else -> null
    }

  /** For when we see `ReplayGain Xing/Info`. */
  private fun handleMp3InfoReplayGain(frame: Mp3InfoReplayGain): Float? {
    return frame.field1?.gain
  }

  private fun handleVorbisComment(comment: VorbisComment): Float? =
    when (comment.key.uppercase()) {
      "REPLAYGAIN_TRACK_GAIN" -> comment.value.parseReplayGainAdjustment()
      //? R128 gain needs to be divided by `256f`.
      "R128_TRACK_GAIN" -> comment.value.parseReplayGainAdjustment()?.div(256f)
      else -> null
    }
  //#endregion

  //#region [Internal Overloads]
  /** Some replay gain tags include "dB" in the string. */
  private fun String.parseReplayGainAdjustment() =
    replace(Regex("[^\\d.-]"), "").toFloatOrNull()
  //#endregion
}
