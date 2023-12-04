package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.MediaRepository
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.room.db.AppDatabase
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val mediaRepository: MediaRepository
    val getMedias: LiveData<MutableList<MediaModel>>

    init {
        val mediaDao = AppDatabase.getDatabase(application).mediaDao()
        mediaRepository = MediaRepository(mediaDao)
        getMedias = mediaRepository.getMedias
    }

    fun insert(mediaModel: MediaModel){
        viewModelScope.launch {
            mediaRepository.insert(mediaModel)
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            mediaRepository.deleteAll()
        }
    }

    /**
     *  Image
     * */
    val uriImage: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val imageProjection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATA,
        )
    } else {
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATA,
        )
    }

    /**
     * Video
     * */

    val uriVideo: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    val videoProjection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DURATION,
    )


    /**
     * Function scan media
     * */
    fun getMediaFile(context: Context): ArrayList<MediaModel>{

        val mListMediaModel = ArrayList<MediaModel>()
        try {
            val cursorImage: Cursor? = context.contentResolver.query(uriImage, imageProjection, null, null, null)
            cursorImage?.let { c ->
                while (c.moveToNext()){
                    val colId = c.getColumnIndex(imageProjection[0])
                    val colName = c.getColumnIndex(imageProjection[1])
                    val colSize = c.getColumnIndex(imageProjection[2])
                    val colData = c.getColumnIndex(imageProjection[3])
                    val file = File(c.getString(colData))
                    if(file.exists()){
                        mListMediaModel.add(
                            MediaModel(c.getLong(colId), c.getString(colName), null, c.getLong(colSize), c.getString(colData), file.lastModified(), Constants.TYPE_IMAGE)
                        )
                    }
                }
            }
            cursorImage?.close()
        }catch (ex: Exception){
            ex.printStackTrace()
        }

        try {
            val cursorVideo: Cursor? = context.contentResolver.query(uriVideo, videoProjection, null, null, null)
            cursorVideo?.let { c ->
                while (c.moveToNext()){
                    val colId = c.getColumnIndex(videoProjection[0])
                    val colName = c.getColumnIndex(videoProjection[1])
                    val colSize = c.getColumnIndex(videoProjection[2])
                    val colData = c.getColumnIndex(videoProjection[3])
                    val mDuration = c.getColumnIndex(videoProjection[4])
                    val duration=c.getLong(mDuration)
                    val file = File(c.getString(colData))
                    if(file.exists()){
                        mListMediaModel.add(
                            MediaModel(c.getLong(colId), c.getString(colName), duration, c.getLong(colSize), c.getString(colData), file.lastModified(), Constants.TYPE_VIDEO)
                        )
                    }
                }
            }
            cursorVideo?.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return mListMediaModel

    }


}