package com.cyanchill.missingcore.metadataretriever.utils

import com.facebook.react.bridge.Promise
import java.util.concurrent.ExecutionException

/**
 * Reusable try/catch wrapper for when we do something pertaining to a file URI. Code that executes
 * in the "try" portion goes in the provided `block`.
 */
fun <T> safeExecuteOnURI(uri: String, errorKey: String, promise: Promise, block: () -> T) {
  try {
    block()
  } catch (e: ExecutionException) {
    val isWantedException =
      e.message?.contains("androidx.media3.datasource.FileDataSource\$FileDataSourceException")
        ?: false
    when (isWantedException) {
      true -> promise.reject("ENOENT", "ENOENT: No such file or directory (${uri})", e)
      false -> promise.reject(errorKey, e.message, e)
    }
  } catch (e: Exception) {
    promise.reject(errorKey, e.message, e)
  }
}
