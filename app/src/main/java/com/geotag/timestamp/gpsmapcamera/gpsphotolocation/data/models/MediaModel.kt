package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "media_table")
@Parcelize
data class MediaModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val duration: Long?,
    val length: Long,
    val data: String,
    val dateTaken: Long,
    val type: Int
): Parcelable