package com.cyanchill.missingcore.metadataretriever.utils

object Normalization {
  /** Returns a string that safely handles special characters such as "%", "?", and "#". */
  fun getSafeUri(uri: String): String {
    // It's important to replace the "%" first as if we put it later on, it'll
    // break the decoding for "?" & "#".
    return uri.replace("%", "%25").replace("?", "%3F").replace("#", "%23")
  }
}
