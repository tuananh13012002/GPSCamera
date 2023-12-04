package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository

import androidx.lifecycle.LiveData
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.Note
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.dao.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val getNotes: LiveData<MutableList<Note>> = noteDao.getNote()

    fun getNoteByPath(path:String):LiveData<Note>{
        return noteDao.getNoteByPath(path)
    }

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }
}