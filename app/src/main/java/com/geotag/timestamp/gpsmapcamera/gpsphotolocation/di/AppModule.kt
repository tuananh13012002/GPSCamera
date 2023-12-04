package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.di

import androidx.paging.ExperimentalPagingApi
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.api.ApiModule
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository.RepositoryModule
import dagger.Module

@OptIn(ExperimentalPagingApi::class)
@Module(
    includes = [
        // module of Activity && ViewModel
        ActivityModule::class,
        ViewModelModule::class,
        // module of data
        ApiModule::class,
        RepositoryModule::class,
        // database
//        DatabaseModule::class,
    ]
)
class AppModule