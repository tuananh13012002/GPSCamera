package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageEntity(
    val name: String?,
    val code: String?,
    val avatar: Int?,
    var isSelected: Boolean = false,
) : Parcelable {
}