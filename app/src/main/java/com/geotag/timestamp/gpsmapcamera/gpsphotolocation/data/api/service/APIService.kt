package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.api.service

import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.ResponseApi
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("character")
    suspend fun getCharacter(@Query("page") page: Int): ResponseApi
}