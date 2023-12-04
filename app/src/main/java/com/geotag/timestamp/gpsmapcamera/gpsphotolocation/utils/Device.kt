package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.d("TAGGGGG", "Error from background: ${throwable.message}")
}