package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main

import androidx.paging.ExperimentalPagingApi
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass.CompassFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map.MapFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.setting.SettingFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@ExperimentalPagingApi
@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFr(): HomeFragment

    @ContributesAndroidInjector
    abstract fun bindMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun bindCompassFragment(): CompassFragment

    @ContributesAndroidInjector
    abstract fun bindSettingFr(): SettingFr


}