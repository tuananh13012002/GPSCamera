package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note?)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getNote(): LiveData<MutableList<Note>>

    @Query("SELECT * FROM note_table WHERE path = :path ORDER BY id DESC LIMIT 1")
    fun getNoteByPath(path: String): LiveData<Note>

    @Delete
    suspend fun delete(note: Note?)

    @Update
    suspend fun update(note: Note?)
}