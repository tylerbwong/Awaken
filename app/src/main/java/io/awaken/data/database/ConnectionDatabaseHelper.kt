package io.awaken.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

import io.awaken.data.model.Connection
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author Tyler Wong
 */
class ConnectionDatabaseHelper internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    val allConnections: Single<List<Connection>>
        get() = Single.fromCallable {
            val connections = ArrayList<Connection>()
            var tempConnection: Connection
            val database = readableDatabase
            var id: Int
            var nickname: String
            var host: String
            var mac: String
            var wolPort: String
            var devPort: String
            var city: String
            var state: String
            var country: String
            var status: String
            var date: String
            val cursor = database.rawQuery(QUERY_ALL_CONNECTIONS, null)
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                nickname = cursor.getString(cursor.getColumnIndex(NICKNAME_COL))
                host = cursor.getString(cursor.getColumnIndex(HOST_COL))
                mac = cursor.getString(cursor.getColumnIndex(MAC_COL))
                wolPort = cursor.getString(cursor.getColumnIndex(WOL_PORT_COL))
                devPort = cursor.getString(cursor.getColumnIndex(DEV_PORT_COL))
                city = cursor.getString(cursor.getColumnIndex(CITY_COL))
                state = cursor.getString(cursor.getColumnIndex(STATE_COL))
                country = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                status = cursor.getString(cursor.getColumnIndex(STATUS_COL))
                date = cursor.getString(cursor.getColumnIndex(DATE_COL))
                tempConnection = Connection(nickname, host, mac, wolPort, devPort,
                        city, state, country, status, date, id)
                connections.add(tempConnection)
                cursor.moveToNext()
            }
            cursor.close()
            connections
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CONNECTIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_CONNECTIONS_TABLE)
        onCreate(db)
    }

    fun insertConnection(connection: Connection?): Completable {
        return Completable.fromCallable {
            val database = writableDatabase
            val contentValues = ContentValues()
            contentValues.put(NICKNAME_COL, connection?.nickname)
            contentValues.put(HOST_COL, connection?.host)
            contentValues.put(MAC_COL, connection?.mac)
            contentValues.put(WOL_PORT_COL, connection?.portWol)
            contentValues.put(DEV_PORT_COL, connection?.portDev)
            contentValues.put(CITY_COL, connection?.city)
            contentValues.put(STATE_COL, connection?.state)
            contentValues.put(COUNTRY_COL, connection?.country)
            contentValues.put(STATUS_COL, connection?.status)
            contentValues.put(DATE_COL, connection?.date)
            database.insertOrThrow(CONNECTIONS_TABLE, null, contentValues)
            null
        }
    }

    fun deleteConnection(id: Int): Boolean {
        val database = writableDatabase
        val idFilter = "$ID_COL='$id'"
        database.delete(CONNECTIONS_TABLE, idFilter, null)
        return true
    }

    fun updateDate(id: Int, date: String) {
        val database = writableDatabase
        val idFilter = "$ID_COL='$id'"
        val contentValues = ContentValues()
        contentValues.put(DATE_COL, date)
        database.update(CONNECTIONS_TABLE, contentValues, idFilter, null)
    }

    fun updateStatus(id: Int, status: String): Single<Boolean> {
        return Single.fromCallable {
            val database = writableDatabase
            val idFilter = "$ID_COL='$id'"
            val contentValues = ContentValues()
            contentValues.put(STATUS_COL, status)
            database.update(CONNECTIONS_TABLE, contentValues, idFilter, null)
            true
        }
    }

    fun getConnection(id: Int): Connection? {
        var connection: Connection? = null
        val database = readableDatabase
        val cursor = database.rawQuery("$QUERY_ALL_CONNECTIONS WHERE $ID_COL = ?", arrayOf(id.toString()))
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            connection = Connection(
                    cursor.getString(cursor.getColumnIndex(NICKNAME_COL)),
            cursor.getString(cursor.getColumnIndex(HOST_COL)),
            cursor.getString(cursor.getColumnIndex(MAC_COL)),
            cursor.getString(cursor.getColumnIndex(WOL_PORT_COL)),
            cursor.getString(cursor.getColumnIndex(DEV_PORT_COL)),
            cursor.getString(cursor.getColumnIndex(CITY_COL)),
            cursor.getString(cursor.getColumnIndex(STATE_COL)),
            cursor.getString(cursor.getColumnIndex(COUNTRY_COL)),
            cursor.getString(cursor.getColumnIndex(STATUS_COL)),
            cursor.getString(cursor.getColumnIndex(DATE_COL)))
            cursor.moveToNext()
        }
        cursor.close()
        return connection
    }

    companion object {
        private val DATABASE_NAME = "Awaken.db"
        private val CONNECTIONS_TABLE = "Connections"
        private val ID_COL = "_id"
        private val NICKNAME_COL = "nickname"
        private val HOST_COL = "host"
        private val MAC_COL = "mac"
        private val WOL_PORT_COL = "portWol"
        private val DEV_PORT_COL = "portDev"
        private val CITY_COL = "city"
        private val STATE_COL = "state"
        private val COUNTRY_COL = "country"
        private val STATUS_COL = "status"
        private val DATE_COL = "lastWoken"
        private val CREATE_CONNECTIONS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                "Connections(_id INTEGER PRIMARY KEY, nickname VARCHAR, host VARCHAR, mac VARCHAR, portWol VARCHAR, " +
                "portDev VARCHAR, city VARCHAR, state VARCHAR, country VARCHAR, status VARCHAR, " +
                "lastWoken VARCHAR)"
        private val DROP_CONNECTIONS_TABLE = "DROP TABLE IF EXISTS Connections"
        private val QUERY_ALL_CONNECTIONS = "SELECT * FROM Connections"
    }
}
