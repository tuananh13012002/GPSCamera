package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.di

import androidx.paging.ExperimentalPagingApi
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.camera.CameraActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.DetailActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass.CompassAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map.MapAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.intro.IntroAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainModule
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaModule
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang.MultiLangAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.permission.PermissionAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.splash.SplashActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.stamp.StampOptionAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.style.StyleAct
import dagger.Module
import dagger.android.ContributesAndroidInjector


@ExperimentalPagingApi
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeMultiLangAct(): MultiLangAct

    @ContributesAndroidInjector
    abstract fun contributeIntroAct(): IntroAct

    @ContributesAndroidInjector
    abstract fun contributePermissionAct(): PermissionAct

    @ContributesAndroidInjector
    abstract fun contributeStampOptionAct(): StampOptionAct

    @ContributesAndroidInjector
    abstract fun contributeStyleAct(): StyleAct

    @ContributesAndroidInjector
    abstract fun contributeCameraActivity(): CameraActivity


    @ContributesAndroidInjector(modules = [MediaModule::class])
    abstract fun bindMediaActivity(): MediaActivity

    @ContributesAndroidInjector
    abstract fun bindDetailActivity(): DetailActivity

    @ContributesAndroidInjector
    abstract fun bindMapAct(): MapAct

    @ContributesAndroidInjector
    abstract fun bindCompassAct(): CompassAct
}