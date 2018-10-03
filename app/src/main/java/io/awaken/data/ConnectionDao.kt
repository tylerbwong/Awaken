package io.awaken.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.awaken.data.model.Connection

@Dao
interface ConnectionDao {
    @Query("SELECT * from connection_table")
    fun getAllConnections(): LiveData<List<Connection>>
}
