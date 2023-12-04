package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.MediaRepository
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.db.AppDatabase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val mediaRepository: MediaRepository
    val getMedias: LiveData<MutableList<MediaModel>>

    init {
        val mediaDao = AppDatabase.getDatabase(application).mediaDao()
        mediaRepository = MediaRepository(mediaDao)
        getMedias = mediaRepository.getMedias
    }

    fun delete(mediaModel: MediaModel){
        viewModelScope.launch {
            mediaRepository.delete(mediaModel)
        }
    }
}