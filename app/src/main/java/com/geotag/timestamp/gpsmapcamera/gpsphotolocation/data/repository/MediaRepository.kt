package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository

import androidx.lifecycle.LiveData
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao.MediaDao

class MediaRepository (private val mediaDao: MediaDao) {
    val getMedias: LiveData<MutableList<MediaModel>> = mediaDao.getMedias()

    suspend fun insert(mediaModel: MediaModel) {
        mediaDao.insert(mediaModel)
    }
    suspend fun delete(mediaModel: MediaModel) {
        mediaDao.delete(mediaModel)
    }
    suspend fun update(mediaModel: MediaModel) {
        mediaDao.update(mediaModel)
    }

    suspend fun deleteAll() {
        mediaDao.deleteAll()
    }
}