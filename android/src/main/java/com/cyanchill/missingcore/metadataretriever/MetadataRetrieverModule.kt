package com.cyanchill.missingcore.metadataretriever

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.os.storage.StorageManager
import androidx.media3.common.MediaMetadata
import java.util.concurrent.ExecutionException


class MetadataRetrieverModule internal constructor(reactContext: ReactApplicationContext) :
  MetadataRetrieverSpec(reactContext) {
  private val context = reactContext

  override fun getTypedExportedConstants(): Map<String, Any?> {
    val constants: MutableMap<String, Any?> = HashMap()
    constants["MusicDirectoryPath"] = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
    constants["StorageVolumesDirectoryPaths"] = this.reactApplicationContext.getExternalFilesDirs(null).mapNotNull { it.absolutePath.split("/Android")[0] }
    constants["PrimaryDirectoryPath"] = Environment.getExternalStorageDirectory()?.absolutePath
    return constants
  }

  @ReactMethod
  override fun getMetadata(uri: String, options: ReadableArray, promise: Promise) {
    // Populate return object with default values based on input.
    val metadataMap = Arguments.createMap()
    for (i in 0 until options.size()) {
      metadataMap.putNull(options.getString(i))
    }

    try {
      val formatList = getFormatList(context, uri)
      val mediaMetadata = MediaMetadata.Builder()
        .populateFromMetadata(getMetadataListFromFormatList(formatList))
        .build()
      var mmrMetadata: MediaMetadataRetriever? = null

      // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever`. This is
      // the case with `ID3v1` tags for example.
      if (mediaMetadata.equals(MediaMetadata.EMPTY)) {
        mmrMetadata = MediaMetadataRetriever()
        mmrMetadata.setDataSource(uri)
      }

      // Populate return object with the metadata we found.
      for (i in 0 until options.size()) {
        val field = options.getString(i)
        val fieldData = when (mmrMetadata) {
          null -> readMediaMetadataField(mediaMetadata, field, uri)
          else -> readMMRField(mmrMetadata, field)
        }

        // Use scope functions to help determine output.
        // SEE https://kotlinlang.org/docs/scope-functions.html
        when (field) {
          /** List of fields available on `Format`. */
          "bitrate", "channelCount", "sampleRate" ->
            readFormatField(formatList[0], field)?.let { metadataMap.putInt(field, it as Int) }

          "codecs", "sampleMimeType" ->
            readFormatField(formatList[0], field)?.let { metadataMap.putString(field, it as String) }

          /** List of fields available on `MediaMetadata`. */
          "albumArtist", "albumTitle", "artist", "artworkData", "artworkDataType", "artworkUri",
          "compilation", "composer", "conductor", "description", "displayTitle", "genre", "mediaType",
          "station", "subtitle", "title", "writer" ->
            fieldData?.let { metadataMap.putString(field, it as String) }

          "discNumber", "recordingDay", "recordingMonth", "recordingYear", "releaseDay", "releaseMonth",
          "releaseYear", "totalDiscCount", "totalTrackCount", "trackNumber", "year" ->
            fieldData?.let { metadataMap.putInt(field, it as Int) }

          "isBrowsable", "isPlayable" ->
            fieldData?.let { metadataMap.putBoolean(field, it as Boolean) }

          "overallRating", "userRating" ->
            fieldData?.let { metadataMap.putDouble(field, it as Double) }

//          "extras" ->
//            metadataMap.putNull(field)
        }
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
      if (metadataList.size == 0) {
        val mmrMetadata = MediaMetadataRetriever()
        mmrMetadata.setDataSource(uri)
        promise.resolve(readMMRField(mmrMetadata, "artworkData") as String?)
        return
      }

      // We'll want to return the image designated as "Cover (front)", otherwise return image for "Other".
      var coverImage: String? = null
      var backupImage: String? = null

      for (i in 0 until metadataList.size) {
        val mediaMetadata = MediaMetadata.Builder()
          .populateFromMetadata(metadataList[i])
          .build()

        when (mediaMetadata.artworkDataType) {
          // "Other" Picture Type
          0 -> {
            if (backupImage == null) {
              backupImage = readMediaMetadataField(mediaMetadata, "artworkData", uri) as String?
            }
          }
          // "Cover (front)" Picture Type
          3 -> { coverImage = readMediaMetadataField(mediaMetadata, "artworkData", uri) as String? }
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
