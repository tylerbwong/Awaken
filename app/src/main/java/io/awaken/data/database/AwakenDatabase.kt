package io.awaken.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.awaken.data.ConnectionDao
import io.awaken.data.model.Connection

@Database(entities = [Connection::class], version = 1)
abstract class AwakenDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao
}
