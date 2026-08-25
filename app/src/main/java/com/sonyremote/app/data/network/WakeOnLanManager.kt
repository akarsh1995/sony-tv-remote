package com.sonyremote.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Wake-on-LAN (WoL) helper to wake suspended Sony TVs via UDP Magic Packets.
 */
object WakeOnLanManager {

    private const val DEFAULT_PORT = 9
    private const val ALT_PORT = 7

    /**
     * Sends a Wake-on-LAN magic packet to the target MAC address.
     * @param macAddress format like "00:11:22:33:44:55" or "00-11-22-33-44-55" or "001122334455"
     */
    suspend fun sendMagicPacket(
        macAddress: String,
        broadcastIp: String = "255.255.255.255"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanMac = macAddress.replace(":", "").replace("-", "").trim()
            if (cleanMac.length != 12) {
                return@withContext Result.failure(IllegalArgumentException("Invalid MAC address: $macAddress"))
            }

            val macBytes = ByteArray(6)
            for (i in 0 until 6) {
                macBytes[i] = cleanMac.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }

            // Construct magic packet: 6 bytes of 0xFF followed by 16 repetitions of MAC
            val bytes = ByteArray(6 + 16 * macBytes.size)
            for (i in 0 until 6) {
                bytes[i] = 0xFF.toByte()
            }
            var offset = 6
            for (i in 0 until 16) {
                System.arraycopy(macBytes, 0, bytes, offset, macBytes.size)
                offset += macBytes.size
            }

            val broadcastAddress = InetAddress.getByName(broadcastIp)
            val socket = DatagramSocket()
            socket.broadcast = true

            // Send to port 9 and port 7 multiple times for high reliability
            for (repeat in 0 until 3) {
                val packet9 = DatagramPacket(bytes, bytes.size, broadcastAddress, DEFAULT_PORT)
                val packet7 = DatagramPacket(bytes, bytes.size, broadcastAddress, ALT_PORT)
                socket.send(packet9)
                socket.send(packet7)
            }
            socket.close()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
