package com.example.tylerbwong.awaken

import android.app.Application
import com.example.tylerbwong.awaken.network.LocationServiceProvider

class AwakenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationServiceProvider.init()
    }
}