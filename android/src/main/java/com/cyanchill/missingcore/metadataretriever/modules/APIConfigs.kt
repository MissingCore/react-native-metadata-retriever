package com.cyanchill.missingcore.metadataretriever.modules

import android.os.Bundle

import com.cyanchill.missingcore.metadataretriever.utils.BundleUtils

open class APIConfigs {
  /**
   * Supported values:
   *  - MAX_IMAGE_SIZE_MB: Double?
   */
  var apiConfigs = Bundle()

  /** Partially update `apiConfigs` based on set values. */
  fun updateConfigs(options: Bundle) {
    BundleUtils.putDoubleIfExists(MAX_IMAGE_SIZE_MB, options, apiConfigs)
  }

  companion object {
    const val MAX_IMAGE_SIZE_MB = "maxImageSizeMB"
  }
}
