package com.cyanchill.missingcore.metadataretriever.modules

import android.media.MediaMetadataRetriever
import android.os.Bundle
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
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import java.util.concurrent.ExecutionException


@OptIn(UnstableApi::class)
class MetadataRetrieverModule(reactContext: ReactApplicationContext) :
  NativeMetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  private var reader = MetadataReader(reactContext)

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
   * as "Cover (front)" and falls back to "Other".
   *
   * Either returns the URI to the saved artwork or a base64 image string.
   */
  override fun getArtwork(uri: String, options: ReadableMap, promise: Promise) {
    val artworkOptions = ArtworkOptions(options)
    val asBase64 = artworkOptions.asBase64

    try {
      val metadataList = getMetadataList(getFormatList(uri))

      // We'll want to return the image designated as "Cover (front)", otherwise return image for
      // "32x32 pixels 'file icon' (PNG only)" or "Other".
      var coverImage: Any? = null
      var backupImage: Any? = null
      var backupImageCode: Int? = null

      // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever`.
      if (metadataList.isEmpty()) {
        val mmrMetadata = MediaMetadataRetriever()
        mmrMetadata.setDataSource(Normalization.getSafeUri(uri))
        coverImage = if (asBase64) reader.getBase64Image(mmrMetadata.embeddedPicture) else mmrMetadata.embeddedPicture
        mmrMetadata.release()
      }

      for (metadataItem in metadataList) {
        val mediaMetadata = MediaMetadata.Builder()
          .populateFromMetadata(metadataItem)
          .build()

        when (mediaMetadata.artworkDataType) {
          // "Other" Picture Type
          MediaMetadata.PICTURE_TYPE_OTHER,
          // Sometimes "Other" Picture Type is set to `-1` (encountered with a `.flac` file)
          -1 -> {
            if (backupImage == null || backupImageCode == 1) {
              val newImg = if (asBase64) reader.getBase64Image(mediaMetadata.artworkData) else mediaMetadata.artworkData
              if (newImg !== null) {
                backupImage = newImg
                backupImageCode = 3
              }
            }
          }
          // "32x32 pixels 'file icon' (PNG only)" Picture Type
          MediaMetadata.PICTURE_TYPE_FILE_ICON -> {
            if (backupImage == null) {
              backupImage = if (asBase64) reader.getBase64Image(mediaMetadata.artworkData) else mediaMetadata.artworkData
              backupImageCode = 1
            }
          }
          // "Cover (front)" Picture Type
          MediaMetadata.PICTURE_TYPE_FRONT_COVER -> {
            coverImage = if (asBase64) reader.getBase64Image(mediaMetadata.artworkData) else mediaMetadata.artworkData
          }
        }

        if (coverImage !== null) break
      }

      if (asBase64) {
        // `coverImage` or `backupImage` should be a base64 string or `null`.
        promise.resolve(coverImage ?: backupImage)
      } else {
        val imgUri = (coverImage ?: backupImage)?.let { reader.saveImage(it as ByteArray, artworkOptions) }
        promise.resolve(imgUri)
      }
    } catch (e: ExecutionException) {
      val isWantedException =
        e.message?.contains("androidx.media3.datasource.FileDataSource\$FileDataSourceException")
          ?: false
      when (isWantedException) {
        true -> promise.reject("ENOENT", "ENOENT: No such file or directory (${uri})", e)
        false -> promise.reject("ERR_ARTWORK", e.message, e)
      }
    } catch (e: Exception) {
      promise.reject("ERR_ARTWORK", e.message, e)
    }
  }

  /** Expose to the user the ability to update internal configuration options. */
  override fun updateConfigs(options: ReadableMap, promise: Promise) {
    reader.updateConfigs(Arguments.toBundle(options) as Bundle)
    promise.resolve(null)
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

  companion object {
    const val NAME = NativeMetadataRetrieverSpec.NAME
  }
}
