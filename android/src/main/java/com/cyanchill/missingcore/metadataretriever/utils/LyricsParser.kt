package com.cyanchill.missingcore.metadataretriever.utils

import androidx.annotation.OptIn
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.BinaryFrame
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.metadata.vorbis.VorbisComment

@OptIn(UnstableApi::class)
class LyricsParser(entry: Metadata.Entry) {
  var lyrics: String? = null
  var isSync = false

  init {
    // Extra lyrics based on the class.
    when (entry) {
      is TextInformationFrame -> handleTextInformationFrame(entry)
      is BinaryFrame -> handleBinaryFrame(entry)
      is VorbisComment -> handleVorbisComment(entry)
    }

    // Sanitize input
    lyrics = lyrics?.replace("\u0000", "")
  }

  private fun handleTextInformationFrame(frame: TextInformationFrame) {
    val tagName = frame.id.uppercase()
    if (tagName !in ID3v2_LYRIC_TAGS) return
    lyrics = frame.values.firstOrNull()
    isSync = tagName == "SYLT"
  }

  private fun handleBinaryFrame(frame: BinaryFrame) {
    val tagName = frame.id.uppercase()
    if (tagName !in ID3v2_LYRIC_TAGS) return

    val byteArr = frame.data
    // The 1st byte in the array determines the encoding in ID3.
    //  - Mp3Tag doesn't specify a Byte Order Mark if it's `1` (UTF-16), so we'll do a heuristic guess for what charset to use.
    //  - Ref: https://mutagen-specs.readthedocs.io/en/latest/id3/id3v2.4.0-structure.html#id3v2-frame-overview
    val encodingCharSet = when (byteArr[0].toString()) {
      "0" -> Charsets.ISO_8859_1
      "1" -> {
        if (byteArr[1] == BYTE_0xFE && byteArr[2] == BYTE_0xFF) {
          Charsets.UTF_16BE
        } else if (byteArr[1] == BYTE_0xFF && byteArr[2] == BYTE_0xFE) {
          Charsets.UTF_16LE
        } else {
          // Heuristic guess of byte order using new line character.
          //  - If the conversion contains a newline character, that charset should be used.
          if (String(byteArr, Charsets.UTF_16LE).contains("\n")) Charsets.UTF_16LE
          else Charsets.UTF_16BE
        }
      }
      "2" -> Charsets.UTF_16BE
      else -> Charsets.UTF_8
    }

    lyrics = String(byteArr, encodingCharSet)
    isSync = tagName == "SYLT"
  }

  private fun handleVorbisComment(comment: VorbisComment) {
    if (comment.key.uppercase() != "LYRICS") return
    // We immediately bail out if we find lyrics in Vorbis Comments due to
    // there being no specific tag for synchronized lyrics.
    lyrics = comment.value
    isSync = true
  }

  companion object {
    private val ID3v2_LYRIC_TAGS = listOf("SYLT", "USLT")
    private const val BYTE_0xFE = 0xFE.toByte()
    private const val BYTE_0xFF = 0xFF.toByte()
  }
}
