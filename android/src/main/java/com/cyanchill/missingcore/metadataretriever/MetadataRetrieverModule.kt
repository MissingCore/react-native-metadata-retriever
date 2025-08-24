package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise

import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import java.util.concurrent.ExecutionException

import com.cyanchill.missingcore.metadataretriever.models.FormatMetadataItem
import com.cyanchill.missingcore.metadataretriever.models.MediaMetadataItem
import com.cyanchill.missingcore.metadataretriever.models.MediaMetadataRetrieverItem
import com.cyanchill.missingcore.metadataretriever.utils.BridgeUtils
import com.cyanchill.missingcore.metadataretriever.utils.MediaMetadataUtils
import com.cyanchill.missingcore.metadataretriever.utils.NormalizationUtils


@OptIn(UnstableApi::class)
class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    val optionsList = BridgeUtils.readableStringArrayToList(options)

    // Populate return object with default values based on input.
    val metadataMap = Arguments.createMap()
    optionsList.forEach { fieldName -> metadataMap.putNull(fieldName) }
    var wantArtwork = optionsList.any { fieldName -> fieldName == "artworkData" }

    // Move outside of try-catch block so we can release it in finally.
    var mmrMetadata: MediaMetadataRetriever? = null

    try {
      val formatList = getFormatList(context, uri)
      val mediaMetadata = MediaMetadata.Builder()
        .populateFromMetadata(getMetadataListFromFormatList(formatList))
        .build()

      // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever` (in the case
      // with `ID3v1` tags).
      if (mediaMetadata == MediaMetadata.EMPTY) {
        mmrMetadata = MediaMetadataRetriever()
        mmrMetadata.setDataSource(NormalizationUtils.getSafeUri(uri))
      }

      val formatMetadataData = FormatMetadataItem(formatList[0])
      val metadataData = when (mmrMetadata) {
        null -> MediaMetadataItem(mediaMetadata, wantArtwork)
        else -> MediaMetadataRetrieverItem(mmrMetadata, wantArtwork)
      }

      var recheckBitRate = false
      // Populate return object with the metadata we found.
      for (field in optionsList) {
        // Use scope functions to help determine output.
        // SEE https://kotlinlang.org/docs/scope-functions.html
        when (field) {
          /** List of fields available on `Format`. */
          "bitrate" -> {
            var foundBitRate = formatMetadataData.bitrate
            if (foundBitRate != null) {
              metadataMap.putInt(field, foundBitRate)
              // Recheck bitrate if less than 96kbps as the value should typically be greater than this.
              // This also handles the case where variable bitrate isn't probably returned as I've seen
              // it be set to `64000`.
              if (foundBitRate < 96000) recheckBitRate = true
            } else recheckBitRate = true
          }
          "channelCount" -> formatMetadataData.channelCount?.let { metadataMap.putInt(field, it) }
          "codecs" -> formatMetadataData.codecs?.let { metadataMap.putString(field, it) }
          "sampleMimeType" -> formatMetadataData.sampleMimeType?.let { metadataMap.putString(field, it) }
          "sampleRate" -> formatMetadataData.sampleRate?.let { metadataMap.putInt(field, it) }

          /** List of fields available on `MediaMetadata`. */
          "albumArtist" -> metadataData.albumArtist?.let { metadataMap.putString(field, it) }
          "albumTitle" -> metadataData.albumTitle?.let { metadataMap.putString(field, it) }
          "artist" -> metadataData.artist?.let { metadataMap.putString(field, it) }
          "artworkData" -> metadataData.artworkData?.let { metadataMap.putString(field, it) }
          "artworkDataType" -> metadataData.artworkDataType?.let { metadataMap.putString(field, it) }
          "artworkUri" -> metadataData.artworkUri?.let { metadataMap.putString(field, it) }
          "compilation" -> metadataData.compilation?.let { metadataMap.putString(field, it) }
          "composer" -> metadataData.composer?.let { metadataMap.putString(field, it) }
          "conductor" -> metadataData.conductor?.let { metadataMap.putString(field, it) }
          "description" -> metadataData.description?.let { metadataMap.putString(field, it) }
          "discNumber" -> metadataData.discNumber?.let { metadataMap.putInt(field, it) }
          "displayTitle" -> metadataData.displayTitle?.let { metadataMap.putString(field, it) }
          // "extras" -> metadataMap.putNull(field)
          "genre" -> metadataData.genre?.let { metadataMap.putString(field, it) }
          "isBrowsable" -> metadataData.isBrowsable?.let { metadataMap.putBoolean(field, it) }
          "isPlayable" -> metadataData.isPlayable?.let { metadataMap.putBoolean(field, it) }
          "mediaType" -> metadataData.mediaType?.let { metadataMap.putString(field, it) }
          "overallRating" -> metadataData.overallRating?.let { metadataMap.putDouble(field, it) }
          "recordingDay" -> metadataData.recordingDay?.let { metadataMap.putInt(field, it) }
          "recordingMonth" -> metadataData.recordingMonth?.let { metadataMap.putInt(field, it) }
          "recordingYear" -> metadataData.recordingYear?.let { metadataMap.putInt(field, it) }
          "releaseDay" -> metadataData.releaseDay?.let { metadataMap.putInt(field, it) }
          "releaseMonth" -> metadataData.releaseMonth?.let { metadataMap.putInt(field, it) }
          "releaseYear" -> metadataData.releaseYear?.let { metadataMap.putInt(field, it) }
          "station" -> metadataData.station?.let { metadataMap.putString(field, it) }
          "subtitle" -> metadataData.subtitle?.let { metadataMap.putString(field, it) }
          "title" -> metadataData.title?.let { metadataMap.putString(field, it) }
          "totalDiscCount" -> metadataData.totalDiscCount?.let { metadataMap.putInt(field, it) }
          "totalTrackCount" -> metadataData.totalTrackCount?.let { metadataMap.putInt(field, it) }
          "trackNumber" -> metadataData.trackNumber?.let { metadataMap.putInt(field, it) }
          "userRating" -> metadataData.userRating?.let { metadataMap.putDouble(field, it) }
          "writer" -> metadataData.writer?.let { metadataMap.putString(field, it) }
          /* List of custom fields derived from other fields. */
          "year" -> metadataData.year?.let { metadataMap.putInt(field, it) }
        }
      }

      // Compute bitrate using `MediaMetadataRetriever` if wanted and not found. Necessary for FLAC
      // files as `Format` doesn't populate that field for whatever reason.
      if (recheckBitRate) {
        // Ensure the `MediaMetadataRetriever` object exists.
        if (mmrMetadata == null) {
          mmrMetadata = MediaMetadataRetriever()
          mmrMetadata.setDataSource(NormalizationUtils.getSafeUri(uri))
        }
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

      promise.resolve(metadataMap)
    } catch (e: TrackGroupArrayException) {
      // Return default wanted metadata map where all fields are `null`.
      promise.resolve(metadataMap)
    } catch (e: ExecutionException) {
      val isWantedException =
        e.message?.contains("androidx.media3.datasource.FileDataSource\$FileDataSourceException")
          ?: false
      when (isWantedException) {
        true -> promise.reject("ENOENT", "ENOENT: No such file or directory (${uri})", e)
        false -> promise.reject("ERR_METADATA", e.message, e)
      }
    } catch (e: Exception) {
      promise.reject("ERR_METADATA", e.message, e)
    } finally {
      // Release `MediaMetadataRetriever` resources.
      mmrMetadata?.release()
    }
  }

  /**
   * Get artwork of audio file from its uri. Unlike getting the artwork from `getMetadata()`, whose
   * artwork is based on the last `artworkData` found, `getArtwork()` returns the artwork designated
   * as "Cover (front)" and falls back to "Other".
   */
  @ReactMethod
  override fun getArtwork(uri: String, promise: Promise) {
    try {
      val metadataList = getMetadataListFromFormatList(getFormatList(context, uri))

      // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever`.
      if (metadataList.isEmpty()) {
        val mmrMetadata = MediaMetadataRetriever()
        mmrMetadata.setDataSource(NormalizationUtils.getSafeUri(uri))
        promise.resolve(MediaMetadataUtils.getBase64Image(mmrMetadata.getEmbeddedPicture()))
        mmrMetadata.release()
        return
      }

      // We'll want to return the image designated as "Cover (front)", otherwise return image for "Other".
      var coverImage: String? = null
      var backupImage: String? = null
      var backupImageCode: Int? = null

      for (metadataItem in metadataList) {
        val mediaMetadata = MediaMetadata.Builder()
          .populateFromMetadata(metadataItem)
          .build()

        when (mediaMetadata.artworkDataType) {
          // "Other" Picture Type
          MediaMetadata.PICTURE_TYPE_OTHER -> {
            if (backupImage == null || backupImageCode == 1) {
              val newImg = MediaMetadataUtils.getBase64Image(mediaMetadata.artworkData)
              if (newImg !== null) {
                backupImage = newImg
                backupImageCode = 3
              }
            }
          }
          // "32x32 pixels 'file icon' (PNG only)" Picture Type
          MediaMetadata.PICTURE_TYPE_FILE_ICON -> {
            if (backupImage == null) {
              backupImage = MediaMetadataUtils.getBase64Image(mediaMetadata.artworkData)
              backupImageCode = 1
            }
          }
          // "Cover (front)" Picture Type
          MediaMetadata.PICTURE_TYPE_FRONT_COVER -> {
            coverImage = MediaMetadataUtils.getBase64Image(mediaMetadata.artworkData)
          }
        }

        if (coverImage !== null) break
      }

      promise.resolve(coverImage ?: backupImage)
    } catch (e: TrackGroupArrayException) {
      promise.resolve(null)
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

  companion object {
    const val NAME = "MetadataRetriever"
  }

  override fun getName(): String = NAME
}
