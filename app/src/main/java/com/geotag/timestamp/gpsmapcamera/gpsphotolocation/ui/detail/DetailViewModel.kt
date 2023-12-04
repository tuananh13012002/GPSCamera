package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.Note
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.MediaRepository
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.NoteRepository
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.db.AppDatabase
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    private val noteRepository: NoteRepository
    val getNotes: LiveData<MutableList<Note>>
    private val mediaRepository: MediaRepository
    val getMedias: LiveData<MutableList<MediaModel>>

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val mediaDao = AppDatabase.getDatabase(application).mediaDao()
        mediaRepository = MediaRepository(mediaDao)
        getMedias = mediaRepository.getMedias

        noteRepository = NoteRepository(noteDao)
        getNotes = noteRepository.getNotes
    }

    fun getNoteByPath(path: String): LiveData<Note> {
        return noteRepository.getNoteByPath(path)
    }


    fun insert(note: Note) {
        viewModelScope.launch {
            noteRepository.insert(note)
        }
    }

    fun delete(mediaModel: MediaModel) {
        viewModelScope.launch {
            mediaRepository.delete(mediaModel)
        }
    }
}