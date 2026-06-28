package com.cyanchill.missingcore.metadataretriever.modules

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.cyanchill.missingcore.metadataretriever.models.ArtworkOptions
import com.cyanchill.missingcore.metadataretriever.utils.Normalization
import com.facebook.react.bridge.ReactApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.net.URLConnection
import java.security.MessageDigest
import java.util.UUID

data class ArtworkSignature(
  val hash: String,
  val bytes: ByteArray,
) {
  companion object {
    fun fromBytes(bytes: ByteArray?): ArtworkSignature? {
      if (bytes == null) return null
      return ArtworkSignature(bytes.toMd5Hex(), bytes)
    }
  }
}

@OptIn(UnstableApi::class)
class ArtworkParser(reactContext: ReactApplicationContext) {
  private val saveDirectory = "${reactContext.cacheDir.absolutePath}${File.separator}MetadataRetriever"

  /** Create `saveDirectory` if it doesn't exist. */
  init {
    try {
      val directory = File(saveDirectory)
      if (!directory.exists()) directory.mkdirs()
    } catch (e: Exception) {}
  }

  //#region [Extractor]
  fun extractArtwork(uri: String, metadataList: List<Metadata>): ArtworkSignature? {
    val isFLAC = uri.endsWith(".flac") || uri.endsWith(".m4a") || uri.endsWith(".mp4")

    // We'll want to return the image designated as "Cover (front)", otherwise return first image found.
    var coverImage: ArtworkSignature? = null
    var backupImage: ArtworkSignature? = null

    // Fallback to `MediaMetadataRetriever` if we find nothing with `MetadataRetriever` or with
    // flac/mp4/m4a files due to artwork not being parsed correctly.
    //  - https://github.com/MissingCore/Music/issues/432
    if (metadataList.isEmpty() || isFLAC) {
      val mmrMetadata = MediaMetadataRetriever()
      mmrMetadata.setDataSource(Normalization.getSafeUri(uri))
      coverImage = ArtworkSignature.fromBytes(mmrMetadata.embeddedPicture)
      mmrMetadata.release()
    }

    for (metadataItem in metadataList) {
      val mediaMetadata = MediaMetadata.Builder()
        .populateFromMetadata(metadataItem)
        .build()

      when (mediaMetadata.artworkDataType) {
        // "Cover (front)" Picture Type
        MediaMetadata.PICTURE_TYPE_FRONT_COVER -> {
          coverImage = ArtworkSignature.fromBytes(mediaMetadata.artworkData)
        }
        // Fallback to 1st image found.
        else -> {
          if (backupImage == null) {
            backupImage = ArtworkSignature.fromBytes(mediaMetadata.artworkData)
          }
        }
      }

      if (coverImage !== null) break
    }

    return coverImage ?: backupImage
  }
  //#endregion

  //#region ["Formatters"]
  fun asBase64(bytes: ByteArray): String? {
    if (!isBase64Convertible(bytes)) return null
    return "data:${getMimeType(bytes)};base64,${Base64.encodeToString(bytes, Base64.DEFAULT)}"
  }

  fun asFile(bytes: ByteArray, options: ArtworkOptions): String? {
    try {
      // Generate path to save image if we didn't provide one.
      val imgUri = options.saveUri ?: "$saveDirectory${File.separator}${UUID.randomUUID()}${options.format.fileExtension}"
      val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
      FileOutputStream(imgUri).use { fos ->
        bitmap.compress(
          options.format.compressFormat,
          (options.compress * 100).toInt(),
          fos,
        )
        fos.flush()
      }
      return Uri.fromFile(File(imgUri)).toString()
    } catch (e: Exception) {
      return null
    }
  }
  //#endregion

  //#region [Helpers]
  /** Determines mimetype from bytes. */
  fun getMimeType(bytes: ByteArray): String? {
    return URLConnection.guessContentTypeFromStream(bytes.inputStream())?.let {
      MimeTypes.normalizeMimeType(it)
    }
  }

  /** Determines if ByteArray can be converted as a base64 string based on our constraints. */
  fun isBase64Convertible(bytes: ByteArray?): Boolean {
    if (bytes == null) return false
    // Ensure the mimeType we get is defined and is for an image.
    if (!MimeTypes.isImage(getMimeType(bytes))) return false
    // Convert max MB to bytes. We take 3/4 of the max MB as converting a byte array to a base64
    // string causes a 33% increase in size.
    val maxSizeBytes = maxImgSizeMB * 0.75 * 1024 * 1024
    return bytes.size <= maxSizeBytes
  }
  //#endregion

  companion object {
    var maxImgSizeMB: Double = 5.0
  }
}

/** Get an MD5 hash as a 32-character hexadecimal string. */
fun ByteArray.toMd5Hex(): String {
  val md = MessageDigest.getInstance("MD5")
  val digest = md.digest(this)
  return digest.joinToString("") { "%02x".format(it) }
}
