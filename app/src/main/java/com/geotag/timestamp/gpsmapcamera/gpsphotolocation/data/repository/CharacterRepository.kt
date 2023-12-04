package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository

import androidx.paging.PagingData
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.ResultEntity
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacter(page: Int): Flow<PagingData<ResultEntity>>
}