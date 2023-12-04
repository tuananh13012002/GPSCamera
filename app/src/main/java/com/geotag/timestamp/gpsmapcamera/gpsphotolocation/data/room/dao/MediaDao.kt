package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mediaModel: MediaModel?)

    @Query("SELECT * FROM media_table ORDER BY id DESC")
    fun getMedias(): LiveData<MutableList<MediaModel>>

    @Delete
    suspend fun delete(mediaModel: MediaModel?)

    @Update
    suspend fun update(mediaModel: MediaModel?)

    @Query("DELETE FROM media_table")
    suspend fun deleteAll()
}