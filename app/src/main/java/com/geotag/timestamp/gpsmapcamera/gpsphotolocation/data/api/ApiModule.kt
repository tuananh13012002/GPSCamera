package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.api

import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.api.service.APIService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun getInstance(): Retrofit {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)
            .connectTimeout(6, TimeUnit.MINUTES)
            .readTimeout(6, TimeUnit.MINUTES)
            .callTimeout(6, TimeUnit.MINUTES)
            .writeTimeout(6, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder().baseUrl("https://rickandmortyapi.com/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }

    @Singleton
    @Provides
    fun getRetrofitService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }
}