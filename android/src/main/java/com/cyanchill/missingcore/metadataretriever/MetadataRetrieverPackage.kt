package com.cyanchill.missingcore.metadataretriever

import com.cyanchill.missingcore.metadataretriever.modules.MetadataRetrieverModule
import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class MetadataRetrieverPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return if (name == MetadataRetrieverModule.NAME) {
      MetadataRetrieverModule(reactContext)
    } else {
      null
    }
  }

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      MetadataRetrieverModule.NAME to ReactModuleInfo(
        name = MetadataRetrieverModule.NAME,
        className = MetadataRetrieverModule.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = false,
        isCxxModule = false,
        isTurboModule = true
      )
    )
  }
}
