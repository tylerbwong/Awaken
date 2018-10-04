package io.awaken.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.awaken.data.network.isRunning
import io.reactivex.Single

/**
 * @author Tyler Wong
 */
@Entity(tableName = "connection_table")
data class Connection(
        val nickname: String?,
        val host: String?,
        val mac: String?,
        val portWol: String?,
        val portDev: String?,
        val city: String?,
        val state: String?,
        val country: String?,
        val status: String,
        val date: String?,
        @PrimaryKey @ColumnInfo(name = "id") val id: Int = 0
        ) {
    fun isRunning(): Single<Boolean> = isRunning(host, Integer.parseInt(portDev))
}
