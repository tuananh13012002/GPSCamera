package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.hasPermission(permissions: Array<String>): Boolean{
    var granted = true
    for (per in permissions){
        if (ContextCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_DENIED){
            granted = false
            break
        }
    }
    return granted
}

fun Context.toast(stringRes: Int, length: Int = Toast.LENGTH_SHORT){
    toast(getString(stringRes), length)
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT){
    CoroutineScope(Dispatchers.Main).launch {
        doToast(this@toast, msg, length)
    }
}

private fun doToast(context: Context, msg: String, length: Int){
    if (context is Activity){
        if (!context.isDestroyed && !context.isFinishing){
            Toast.makeText(context, msg, length).show()
        }
    }else{
        Toast.makeText(context, msg, length).show()
    }
}



