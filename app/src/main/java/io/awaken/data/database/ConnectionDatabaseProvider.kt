package io.awaken.data.database

import android.content.Context

object ConnectionDatabaseProvider {
    lateinit var databaseHelper: ConnectionDatabaseHelper

    fun init(context: Context) {
        databaseHelper = ConnectionDatabaseHelper(context)
    }
}
