package com.example.tylerbwong.awaken.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationService {
    @GET("{ipAddress}?access_key=c82976543e389311064e7e8d4a0dc1a7")
    fun getLocation(@Path("ipAddress") ipAddress: String): Single<Location>
}