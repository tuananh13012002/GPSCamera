package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models

import com.google.gson.annotations.SerializedName

class ResultEntity(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String? = null
)
