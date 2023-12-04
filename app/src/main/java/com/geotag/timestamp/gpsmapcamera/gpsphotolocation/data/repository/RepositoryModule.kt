package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.repository

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun providesCharacterRepository(characterRepositoryImpl: CharacterRepositoryImpl): CharacterRepository
}