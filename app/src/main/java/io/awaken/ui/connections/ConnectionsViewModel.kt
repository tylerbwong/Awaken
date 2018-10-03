package io.awaken.ui.connections

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.awaken.data.database.ConnectionDatabaseProvider
import io.awaken.data.model.Connection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ConnectionsViewModel : ViewModel() {
    private val databaseHelper = ConnectionDatabaseProvider.databaseHelper
    internal val connections = MutableLiveData<List<Connection>>()

    @Suppress("CheckResult")
    private fun refreshConnections() {
        databaseHelper.allConnections
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { connections.value = it },
                        { Log.e("ERROR", it.localizedMessage) }
                )
    }

    @Suppress("CheckResult")
    fun updateStatuses() {
        if (connections.value == null || connections.value?.isEmpty() == true) {
            refreshConnections()
        }

        connections.value?.forEach { connection ->
            val connectionId = connection.id
            connection.isRunning()
                    .flatMap { databaseHelper.updateStatus(connectionId, it.toString()) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { refreshConnections() },
                            { Log.e("ERROR", it.localizedMessage) }
                    )
        }
    }
}
