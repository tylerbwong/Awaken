package io.awaken

import android.app.Application
import io.awaken.data.database.ConnectionDatabaseProvider
import io.awaken.data.network.LocationServiceProvider

class AwakenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConnectionDatabaseProvider.init(this)
        LocationServiceProvider.init()
    }
}