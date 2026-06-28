package com.cyanchill.missingcore.metadataretriever.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap

class ArtworkOptions(options: ReadableMap) {
  /** If we want to return the artwork as a base64 string. */
  val asBase64: Boolean

  /** A value in the range `0.0` - `1.0` specifying the quality of the resulting image. */
  val compress: Double
  /** Specifies the format the image will be saved in. */
  val format: ImageFormat
  /** Location where we want to save the image. */
  val saveUri: String?

  init {
    val optionsBundle = Arguments.toBundle(options) as Bundle
    asBase64 = optionsBundle.getBoolean("base64")

    compress = if (optionsBundle.containsKey("compress")) optionsBundle.getDouble("compress") else 1.0
    // Default format to JPEG if it's not provided.
    format = ImageFormat.fromCode(optionsBundle.getString("format")) ?: ImageFormat.JPEG

    // Remove `file://` in `saveUri` if provided.
    val uri = optionsBundle.getString("saveUri")
    saveUri = if (uri != null) Uri.parse(uri).path else null
  }
}

class HashedArtworkOptions(options: ReadableMap) {
  /** A value in the range `0.0` - `1.0` specifying the quality of the resulting image. */
  val compress: Double
  /** Specifies the format the image will be saved in. */
  val format: ImageFormat
  /** Location where we want to save the image. */
  val saveDirectory: String
  /** A list of know image hashes. */
  val knownHashes: ArrayList<String>

  init {
    val optionsBundle = Arguments.toBundle(options) as Bundle

    compress = if (optionsBundle.containsKey("compress")) optionsBundle.getDouble("compress") else 1.0
    // Default format to JPEG if it's not provided.
    format = ImageFormat.fromCode(optionsBundle.getString("format")) ?: ImageFormat.JPEG

    // Remove `file://` in `saveUri` if provided.
    val uri = optionsBundle.getString("saveDirectory") as String
    saveDirectory = Uri.parse(uri).path as String

    knownHashes = optionsBundle.getStringArrayList("knownHashes") as ArrayList<String>
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
