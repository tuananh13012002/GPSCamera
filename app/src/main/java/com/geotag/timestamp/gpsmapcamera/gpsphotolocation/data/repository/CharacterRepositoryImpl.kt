package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.api.service.APIService
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.ResultEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(private val apiService: APIService) :
    CharacterRepository {
    override suspend fun getCharacter(page: Int): Flow<PagingData<ResultEntity>> =
        Pager(PagingConfig(pageSize = 20)) {
            CharacterPagingData(apiService)
        }.flow

}