package io.awaken.data.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object LocationServiceProvider {
    lateinit var locationService: LocationService

    fun init() {
        locationService = Retrofit.Builder()
                .baseUrl("http://api.ipstack.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationService::class.java)
    }
}