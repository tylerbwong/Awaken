package io.awaken.components

import io.awaken.network.StatusUpdate
import io.reactivex.Single

/**
 * @author Tyler Wong
 */
class Connection(val id: Int, val nickname: String, val host: String, val mac: String, val portWol: String, val portDev: String,
                      val city: String, val state: String, val country: String, val status: String, val date: String) {
    fun isRunning(): Single<Boolean> {
        return StatusUpdate.isRunning(host, Integer.parseInt(portDev));
    }
}
