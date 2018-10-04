@file:JvmName("StatusUpdate")
package io.awaken.data.network

import io.reactivex.Single
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * How long the socket should wait for a response
 * before stopping.
 */
private const val TIMEOUT = 500

/**
 * Gets the current status of the device in question. The method
 * will attempt to establish a socket connection with the target
 * device. If no connection can be made, the device is considered
 * INACTIVE.
 *
 * @param host       - the name of the host/ip address
 * @param devicePort - the port that the router forwards to your device
 * @return true if the current status of the machine is RUNNING or false if the current status is
 * INACTIVE
 */
fun isRunning(host: String?, devicePort: Int): Single<Boolean> {
    return Single.fromCallable {
        var status = true
        try {
            val address = InetSocketAddress(host, devicePort)
            val statusSocket = Socket()
            statusSocket.connect(address, TIMEOUT)
            statusSocket.close()
        }
        catch (e: IOException) {
            status = false
        }

        status
    }
}
