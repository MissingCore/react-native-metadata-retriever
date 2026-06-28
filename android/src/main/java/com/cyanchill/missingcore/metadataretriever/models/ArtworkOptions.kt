package com.cyanchill.missingcore.metadataretriever.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.cyanchill.missingcore.metadataretriever.utils.RequiredArgumentException
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import java.io.File

data class ArtworkOptions(
  /** If the image should be returned as a base64 string. */
  val base64: Boolean = false,
  /** Value in the range of `0.0` - `1.0` specifying the quality of the resulting image. */
  val compress: Double = 1.0,
  /** Format the image will be saved in. */
  val format: ImageFormat = ImageFormat.JPEG,
  /**
   * [Only for Non-Image Hashing Strategy]
   * "Location" artwork will be saved to (includes file name + extension).
   */
  val saveUri: String?,
  /**
   * [Only for Image Hashing Strategy]
   * The directory the file will be saved to.
   */
  val saveDirectory: String?,
  /**
   * [Only for Image Hashing Strategy]
   * A list of image hashes inside of `saveDirectory`.
   */
  val knownHashes: ArrayList<String>?
) {
  fun withGeneratedSaveUri(hash: String): ArtworkOptions {
    if (saveDirectory == null) {
      throw RequiredArgumentException("withGeneratedSaveUri", "saveDirectory")
    }
    return this.copy(
      saveUri = "${saveDirectory}${File.separator}$hash${format.fileExtension}",
    )
  }

  companion object {
    fun fromReadableMap(options: ReadableMap): ArtworkOptions {
      val optionsBundle = Arguments.toBundle(options) as Bundle

      return ArtworkOptions(
        optionsBundle.getBoolean("base64"),
        if (optionsBundle.containsKey("compress")) {
          optionsBundle.getDouble("compress").coerceIn(0.0, 1.0)
        } else 1.0,
        ImageFormat.fromCode(optionsBundle.getString("format")) ?: ImageFormat.JPEG,
        optionsBundle.getString("saveUri")?.let { Uri.parse(it).path },
        optionsBundle.getString("saveDirectory")?.let { Uri.parse(it).path },
        optionsBundle.getStringArrayList("knownHashes"),
      )
    }
  }
}

enum class ImageFormat(val code: String) {
  JPEG("jpeg"),
  JPG("jpg"),
  PNG("png"),
  WEBP("webp");

  val fileExtension: String
    get() = when (this) {
      JPEG, JPG -> ".jpg"
      PNG -> ".png"
      WEBP -> ".webp"
    }

  val compressFormat: Bitmap.CompressFormat
    get() = when (this) {
      JPEG, JPG -> Bitmap.CompressFormat.JPEG
      PNG -> Bitmap.CompressFormat.PNG
      WEBP -> Bitmap.CompressFormat.WEBP
    }

  companion object {
    fun fromCode(code: String?): ImageFormat? {
      if (code == null) return null
      return entries.find { it.code == code }
    }
  }
}
