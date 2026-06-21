package com.cyanchill.missingcore.metadataretriever.modules

import androidx.annotation.OptIn
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.BinaryFrame
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.metadata.vorbis.VorbisComment

private data class ParsedResult(
  val lyrics: String?,
  val isSync: Boolean,
)

@OptIn(UnstableApi::class)
class LyricsParser(metadataList: List<Metadata>) {
  private var isSync = false
  var lyrics: String? = null

  init {
    for (metadata in metadataList) {
      val numEntries = metadata.length()
      for (i in 0 until numEntries) {
        parseMetadataEntry(metadata[i])
        if (isSync) break
      }
      if (isSync) break
    }
  }

  private fun parseMetadataEntry(entry: Metadata.Entry) {
    val result = when (entry) {
      is TextInformationFrame -> handleTextInformationFrame(entry)
      is BinaryFrame -> handleBinaryFrame(entry)
      is VorbisComment -> handleVorbisComment(entry)
      else -> null
    }

    if (result !== null && result.lyrics !== null) {
      // Sanitize input before returning it.
      lyrics = result.lyrics.replace("\u0000", "")
      isSync = result.isSync
    }
  }

  //#region [Lyrics Containers]
  private fun handleTextInformationFrame(frame: TextInformationFrame): ParsedResult? {
    val tagName = frame.id.uppercase()
    if (tagName !in ID3v2_LYRIC_TAGS) return null
    return ParsedResult(frame.values.firstOrNull(), tagName == "SYLT")
  }

  private fun handleBinaryFrame(frame: BinaryFrame): ParsedResult? {
    val tagName = frame.id.uppercase()
    if (tagName !in ID3v2_LYRIC_TAGS) return null

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

    return ParsedResult(String(byteArr, encodingCharSet), tagName == "SYLT")
  }

  private fun handleVorbisComment(comment: VorbisComment): ParsedResult? {
    if (comment.key.uppercase() != "LYRICS") return null
    // We immediately bail out if we find lyrics in Vorbis Comments due to
    // there being no specific tag for synchronized lyrics.
    return ParsedResult(comment.value, true)
  }
  //#endregion

  companion object {
    private val ID3v2_LYRIC_TAGS = listOf("SYLT", "USLT")
    private const val BYTE_0xFE = 0xFE.toByte()
    private const val BYTE_0xFF = 0xFF.toByte()
  }
}
