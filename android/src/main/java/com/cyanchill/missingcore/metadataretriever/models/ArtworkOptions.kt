package com.cyanchill.missingcore.metadataretriever.models

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap

import android.net.Uri
import android.os.Bundle

class ArtworkOptions(options: ReadableMap) {
  /** If we want to return the artwork as a base64 string. */
  val asBase64: Boolean

  /** Location where we want to save the image. */
  val saveUri: String?
  /** Save the image at 80% quality. */
  val compress: Boolean

  init {
    val optionsBundle = Arguments.toBundle(options) as Bundle
    asBase64 = optionsBundle.getBoolean("base64")
    compress = optionsBundle.getBoolean("compress")

    // Remove `file://` in `saveUri` if provided.
    val uri = optionsBundle.getString("saveUri")
    saveUri = if (uri != null) Uri.parse(uri).path else null
  }
}
