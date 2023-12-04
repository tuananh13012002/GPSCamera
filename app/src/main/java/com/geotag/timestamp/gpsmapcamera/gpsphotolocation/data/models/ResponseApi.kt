package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models

import com.google.gson.annotations.SerializedName

class ResponseApi(
    @SerializedName("results")
    var results: MutableList<ResultEntity>? = null
)