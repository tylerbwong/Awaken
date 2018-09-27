package io.awaken

import android.app.Application
import io.awaken.network.LocationServiceProvider

class AwakenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationServiceProvider.init()
    }
}