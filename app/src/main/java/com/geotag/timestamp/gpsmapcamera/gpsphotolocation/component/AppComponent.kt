package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.component


import android.app.Application
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.MyApplication
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.di.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class])
interface AppComponent : AndroidInjector<MyApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}