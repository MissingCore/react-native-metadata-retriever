package com.cyanchill.missingcore.metadataretriever.modules

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.inspector.MetadataRetriever
import com.cyanchill.missingcore.metadataretriever.NativeMetadataRetrieverSpec
import com.cyanchill.missingcore.metadataretriever.models.ArtworkOptions
import com.cyanchill.missingcore.metadataretriever.models.BridgeReturnables.*
import com.cyanchill.missingcore.metadataretriever.utils.MapUtils
import com.cyanchill.missingcore.metadataretriever.utils.Normalization
import com.cyanchill.missingcore.metadataretriever.utils.safeExecuteOnURI
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ExecutionException

@OptIn(UnstableApi::class)
class MetadataRetrieverModule(reactContext: ReactApplicationContext) :
  NativeMetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  private var reader = MetadataReader(reactContext)
  private var artwork = ArtworkParser(reactContext)

  override fun getBulkMetadata(uris: ReadableArray, options: ReadableArray, promise: Promise) {
    val uriList = Arguments.toList(uris) as List<String>
    val optionsList = Arguments.toList(options) as List<String>

    val returnObj = Arguments.createMap()
    val successArr = Arguments.createArray()
    val errorArr = Arguments.createArray()

    // Generate the structure of the object we want to return.
    val returnMetadataStructure = Arguments.createMap()
    optionsList.forEach { fieldName -> returnMetadataStructure.putNull(fieldName) }

    val wantArtwork = optionsList.any { fieldName -> fieldName == "artworkData" }

    // Move outside of try-catch block so we can release it in finally.
    var mmrMetadata = MediaMetadataRetriever()
    var mmrSource: String? = null

    for (uri in uriList) {
      try {
        val metadataMap = returnMetadataStructure.copy()

        val formatList = getFormatList(uri)
        val mediaMetadata = MediaMetadata.Builder()
          .populateFromMetadata(getMetadataList(formatList))
          .build()

        // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever` (in the case
        // with `ID3v1` tags).
        if (mediaMetadata == MediaMetadata.EMPTY) {
          mmrMetadata.setDataSource(Normalization.getSafeUri(uri))
          mmrSource = uri
        }

        val formatMetadataDataMap = reader.fromFormat(formatList[0])
        val metadataDataMap = when (mmrSource == uri) {
          true -> reader.fromMediaMetadataRetriever(mmrMetadata, wantArtwork)
          false -> reader.fromMediaMetadata(mediaMetadata, wantArtwork)
        }

        var recheckBitRate = false
        // Populate return object with the metadata we found.
        for (field in optionsList) {
          // Use scope functions to help determine output.
          // SEE https://kotlinlang.org/docs/scope-functions.html
          when (field) {
            /** List of fields available on `Format`. */
            "bitrate" -> {
              val foundBitRate = MapUtils.getInt(formatMetadataDataMap, "bitrate")
              if (foundBitRate != null) {
                metadataMap.putInt(field, foundBitRate)
                // Recheck bitrate if less than 96kbps as the value should typically be greater than this.
                // This also handles the case where variable bitrate isn't probably returned as I've seen
                // it be set to `64000`.
                if (foundBitRate < 96000) recheckBitRate = true
              } else recheckBitRate = true
            }

            "channelCount", "sampleRate" ->
              MapUtils.getInt(formatMetadataDataMap, field)?.let { metadataMap.putInt(field, it) }

            "codecs", "sampleMimeType" ->
              MapUtils.getString(formatMetadataDataMap, field)?.let { metadataMap.putString(field, it) }

            /** List of fields available on `MediaMetadata`. */
            "albumArtist", "albumTitle", "artist", "artworkData", "artworkDataType", "artworkUri",
            "compilation", "composer", "conductor", "description", "displayTitle", "genre", "mediaType",
            "station", "subtitle", "title", "writer" ->
              MapUtils.getString(metadataDataMap, field)?.let { metadataMap.putString(field, it) }

            "discNumber", "recordingDay", "recordingMonth", "recordingYear", "releaseDay", "releaseMonth",
            "releaseYear", "totalDiscCount", "totalTrackCount", "trackNumber", "year" ->
              MapUtils.getInt(metadataDataMap, field)?.let { metadataMap.putInt(field, it) }

            "isBrowsable", "isPlayable" ->
              MapUtils.getBoolean(metadataDataMap, field)?.let { metadataMap.putBoolean(field, it) }

            "overallRating", "userRating" ->
              MapUtils.getDouble(metadataDataMap, field)?.let { metadataMap.putDouble(field, it) }
          }
        }

        // Compute bitrate using `MediaMetadataRetriever` if wanted and not found. Necessary for FLAC
        // files as `Format` doesn't populate that field for whatever reason.
        if (recheckBitRate) {
          // Ensure the `MediaMetadataRetriever` uses the current URI.
          if (mmrSource != uri) mmrMetadata.setDataSource(Normalization.getSafeUri(uri))
          mmrMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull()?.let { metadataMap.putInt("bitrate", it) }
        }

        // Have `albumArtist` fallback to `artist` value if it's not defined, but only when certain
        // conditions are met (`artist` & `albumTitle` fields are defined).
        if (metadataMap.hasKey("albumArtist") && metadataMap.isNull("albumArtist")) {
          if (
            metadataMap.hasKey("artist") && !metadataMap.isNull("artist") &&
            metadataMap.hasKey("albumTitle") && !metadataMap.isNull("albumTitle")
          ) {
            metadataMap.putString("albumArtist", metadataMap.getString("artist"))
          }
        }

        successArr.pushMap(ResultObject(uri, metadataMap))
      } catch (e: ExecutionException) {
        val isWantedException =
          e.message?.contains("androidx.media3.datasource.FileDataSource\$FileDataSourceException")
            ?: false
        val errObj = ErrorObject(
          if (isWantedException) "ENOENT" else "ERR_METADATA",
          if (isWantedException) "ENOENT: No such file or directory (${uri})" else e.message ?: "",
        )
        errorArr.pushMap(ResultObject(uri, errObj))
      } catch (e: Exception) {
        errorArr.pushMap(ResultObject(uri, ErrorObject("ERR_METADATA", e.message ?: "")))
      }
    }

    // Release `MediaMetadataRetriever` resources.
    mmrMetadata.release()

    returnObj.putArray("results", successArr)
    returnObj.putArray("errors", errorArr)

    promise.resolve(returnObj)
  }

  /**
   * Get artwork of audio file from its uri. Unlike getting the artwork from `getMetadata()`, whose
   * artwork is based on the last `artworkData` found, `getArtwork()` returns the artwork designated
   * as "Cover (front)" and falls back to the first image found.
   *
   * Returns an object containing the hash of the image ByteArray along with a uri or base64 string
   * representing the image.
   */
  override fun getArtwork(uri: String, options: ReadableMap, promise: Promise) {
    val artworkOptions = ArtworkOptions.fromReadableMap(options)
    val asBase64 = artworkOptions.base64

    safeExecuteOnURI(uri, "ERR_ARTWORK", promise) {
      val metadataList = getMetadataList(getFormatList(uri))
      val (hash, bytes) = artwork.extractArtwork(uri, metadataList)
        ?: return@safeExecuteOnURI promise.resolve(null)

      val returnObj = Arguments.createMap().apply {
        putString("hash", hash)
      }

      // Case 1: Return base64 image.
      if (asBase64) {
        val base64Str = artwork.asBase64(bytes)
          ?: return@safeExecuteOnURI promise.resolve(null)
        returnObj.putString("data", base64Str)
        return@safeExecuteOnURI promise.resolve(returnObj)
      }

      // Case 2: Return image independently of hash.
      if (artworkOptions.saveDirectory == null || artworkOptions.knownHashes == null) {
        val imgUri = artwork.asFile(bytes, artworkOptions)
          ?: return@safeExecuteOnURI promise.resolve(null)
        returnObj.putString("data", imgUri)
        return@safeExecuteOnURI promise.resolve(returnObj)
      }

      // Case 3: Return image with respect to hash.
      val hashedArtworkOptions = artworkOptions.withGeneratedSaveUri(hash)
      returnObj.putString("data", Uri.fromFile(File(hashedArtworkOptions.saveUri as String)).toString())

      // If hash isn't known, save the image.
      if (hash !in artworkOptions.knownHashes) {
        // If we failed to save the image, return `null` instead.
        artwork.asFile(bytes, hashedArtworkOptions)
          ?: return@safeExecuteOnURI promise.resolve(null)
      }

      // A "fixed" result for this case.
      promise.resolve(returnObj)
    }
  }

  /** Returns embedded lyrics in supported "lyrics" tags. Prefers returning synchronized lyrics. */
  override fun getLyric(uri: String, promise: Promise) {
    safeExecuteOnURI(uri, "ERR_LYRIC", promise) {
      val metadataList = getMetadataList(getFormatList(uri))
      promise.resolve(LyricsParser(metadataList).lyrics)
    }
  }

  /** Returns the embedded track ReplayGain. */
  override fun getR128Gain(uri: String, promise: Promise) {
    safeExecuteOnURI(uri, "ERR_REPLAY_GAIN", promise) {
      val metadataList = getMetadataList(getFormatList(uri))
      promise.resolve(ReplayGainParser(metadataList).gain)
    }
  }

  override fun debugEmbeddedTags(uri: String, promise: Promise) {
    val returnObj = Arguments.createMap()
    val formatStrArr = Arguments.createArray()
    val metadataStrArr = Arguments.createArray()

    safeExecuteOnURI(uri, "ERR_DEBUG", promise) {
      val formatList = getFormatList(uri)
      formatList.forEach { item -> formatStrArr.pushString(item.toString()) }

      val metadataList = getMetadataList(formatList)
      metadataList.forEach { item -> metadataStrArr.pushString(item.toString()) }

      returnObj.putArray("format", formatStrArr)
      returnObj.putArray("metadata", metadataStrArr)
      promise.resolve(returnObj)
    }
  }

  //#region [Internal Helpers]
  /**
   * Returns a list of `Format` from an uri.
   *
   * @throws ExecutionException If file was not found from uri.
   *
   * @see <a href="https://developer.android.com/media/media3/exoplayer/retrieving-metadata#wo-playback">Link</a>
   */
  private fun getFormatList(uri: String): List<Format> {
    val mediaItem = MediaItem.fromUri(Normalization.getSafeUri(uri))
    MetadataRetriever.Builder(context, mediaItem).build().use { metadataRetriever ->
      val trackGroupArray = metadataRetriever.retrieveTrackGroups().get()
      val formatList = mutableListOf<Format>()
      for (i in 0 until trackGroupArray.length) {
        val trackGroup = trackGroupArray[i]
        // Only care about `TrackGroup` containing audio.
        if (trackGroup.type != C.TRACK_TYPE_AUDIO) continue
        for (j in 0 until trackGroup.length) {
          // By definition, a `TrackGroup` should have at least 1 `Format`.
          // SEE https://developer.android.com/reference/androidx/media3/common/TrackGroup#TrackGroup(androidx.media3.common.Format...)
          formatList.add(trackGroup.getFormat(j))
        }
      }
      return formatList
    }
  }

  /** Returns a list of `Metadata` from `List<Format>`. */
  private fun getMetadataList(formatList: List<Format>): List<Metadata> {
    val metadataList = mutableListOf<Metadata>()
    formatList.forEach {
      it.metadata?.let { metadataList.add(it) }
    }
    return metadataList
  }
  //#endregion

  //#region [Internal Overloads]
  /** Get an MD5 hash as a 32-character hexadecimal string. */
  fun ByteArray.toMd5Hex(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this)
    return digest.joinToString("") { "%02x".format(it) }
  }
  //#endregion

  companion object {
    const val NAME = NativeMetadataRetrieverSpec.NAME
  }
}
