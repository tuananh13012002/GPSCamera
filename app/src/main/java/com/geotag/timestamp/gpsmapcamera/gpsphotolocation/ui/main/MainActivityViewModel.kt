package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.ResultEntity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.CharacterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MainActivityViewModel @Inject constructor
    (private val characterRepository: CharacterRepository) :
    ViewModel() {

    val page = MutableLiveData<Int>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val results: Flow<PagingData<ResultEntity>> = flowOf(page.asFlow().flatMapLatest {
        characterRepository.getCharacter(page.value ?: 1)
    }).flattenMerge(2)

}