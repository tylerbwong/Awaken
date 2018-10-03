package io.awaken.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.awaken.data.ConnectionDao
import io.awaken.data.model.Connection

@Database(entities = [Connection::class], version = 1)
abstract class AwakenDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao

    companion object {
        @Volatile
        private var INSTANCE: AwakenDatabase? = null

        fun getDatabase(context: Context): AwakenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AwakenDatabase::class.java,
                        "awaken_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
