package com.cyanchill.missingcore.metadataretriever.utils

class RequiredArgumentException(funName: String, argName: String) :
    IllegalArgumentException("`$argName` must be defined in order to use `$funName`.")
