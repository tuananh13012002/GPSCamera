package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.DetailViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass.CompassViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map.MapViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainActivityViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel::class)
    abstract fun bindCompassViewModel(compassViewModel: CompassViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MediaViewModel::class)
    abstract fun bindMediaViewModel(mediaViewModel: MediaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(detailViewModel: DetailViewModel): ViewModel
}