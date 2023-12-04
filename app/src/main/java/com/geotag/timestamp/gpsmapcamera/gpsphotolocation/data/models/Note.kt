package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=null,
    val path: String? = null,
    val textNote: String? = null
)
