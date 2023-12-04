package com.geotag.timestamp.gpsmapcamera.gpsphotolocation

import com.appsflyer.AppsFlyerLib
import com.appsflyer.adrevenue.AppsFlyerAdRevenue
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class MyApplication : DaggerApplication() {

    private val applicationInjector = DaggerAppComponent.builder().application(this).build()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector

    override fun onCreate() {
        super.onCreate()
        AppsFlyerLib.getInstance().init(this.getString(R.string.app_flyer), null, this)
        AppsFlyerLib.getInstance().start(this)

        val afRevenueBuilder = AppsFlyerAdRevenue.Builder(this)
        AppsFlyerAdRevenue.initialize(afRevenueBuilder.build())
    }

}

