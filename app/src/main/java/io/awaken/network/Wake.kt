package io.awaken.network

import io.reactivex.Completable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Utility class for sending magic packets to WOL enabled devices.
 *
 * @param host the host name/ip address of the device
 * @param mac  the MAC address of the device
 *
 * @author Connor Wong
 */
object Wake {

    /**
     * The length of the MAC address.
     */
    private const val MAC_LENGTH = 6

    /**
     * Magic packets must be 102 bytes. Length of the UDP packet.
     */
    private const val UDP_MULTIPLIER = 16

    private const val HEXADECIMAL = 16
    /**
     * A delimiter for the target device's MAC address.
     */
    private const val MAC_DELIMITER = "(\\:|\\-)"

    private const val PASS_DELIMITER = "(\\:|\\-)"
    /**
     * Invalid MAC address status message.
     */
    private const val INVALID_MAC = "Invalid MAC Address"

    private const val INVALID_PASS = "Invalid SecureOn Password"

    /**
     * Sends a magic packet to the target device to be woken up.
     */
    @JvmOverloads
    fun sendPacket(host: String, mac: String, pass: String = "", port: Int = 7): Completable {
        return Completable.fromCallable {
            val macBytes = getMacBytes(mac)
            val bytes = ByteArray(MAC_LENGTH + UDP_MULTIPLIER * macBytes.size)

            for (index in 0 until MAC_LENGTH) {
                bytes[index] = 0xff.toByte()
            }

            var index = MAC_LENGTH
            while (index < bytes.size) {
                System.arraycopy(macBytes, 0, bytes, index, macBytes.size)
                index += macBytes.size
            }

            val address = InetAddress.getByName(host)
            val packet = DatagramPacket(bytes, bytes.size, address, port)
            val socket = DatagramSocket()

            socket.send(packet)
            socket.close()
        }
    }

    private fun getMacBytes(macStr: String): ByteArray {
        val bytes = ByteArray(MAC_LENGTH)
        val mac = macStr.split(MAC_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (mac.size != MAC_LENGTH) {
            throw IllegalArgumentException(INVALID_MAC)
        }

        try {
            for (index in 0 until MAC_LENGTH) {
                bytes[index] = Integer.parseInt(mac[index], HEXADECIMAL).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(INVALID_MAC)
        }

        return bytes
    }

    private fun getPassBytes(passStr: String): ByteArray {
        val bytes = ByteArray(passStr.length)
        val pass = passStr.split(PASS_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if ((pass.size != 4) or (pass.size != 6)) {
            throw IllegalArgumentException(INVALID_PASS)
        }

        try {
            for (index in pass.indices) {
                bytes[index] = Integer.parseInt(pass[index], HEXADECIMAL).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(INVALID_PASS)
        }

        return bytes
    }
}
