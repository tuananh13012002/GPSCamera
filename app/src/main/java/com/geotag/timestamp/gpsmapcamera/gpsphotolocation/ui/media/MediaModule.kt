package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media

import androidx.paging.ExperimentalPagingApi
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.fragment.PhotoFr
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.fragment.VideoFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@ExperimentalPagingApi
@Module
abstract class MediaModule {
    @ContributesAndroidInjector
    abstract fun bindPhotoFr(): PhotoFr

    @ContributesAndroidInjector
    abstract fun bindVideoFr(): VideoFr

}