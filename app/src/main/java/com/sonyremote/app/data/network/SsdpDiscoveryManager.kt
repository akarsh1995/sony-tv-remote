package com.sonyremote.app.data.network

import android.content.Context
import android.net.wifi.WifiManager
import com.sonyremote.app.data.model.TvDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URI

/**
 * Discovers Sony Bravia TVs on the local Wi-Fi network via SSDP (UPnP M-SEARCH).
 */
class SsdpDiscoveryManager(private val context: Context) {

    private val ssdpAddress = "239.255.255.250"
    private val ssdpPort = 1900

    /**
     * Scans the local network for Sony Bravia IRCC-enabled devices.
     */
    suspend fun discoverDevices(timeoutMillis: Int = 4000): List<TvDevice> = withContext(Dispatchers.IO) {
        val discoveredDevices = mutableMapOf<String, TvDevice>()
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val multicastLock = wifiManager?.createMulticastLock("sony_remote_ssdp_lock")?.apply {
            setReferenceCounted(true)
            acquire()
        }

        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.soTimeout = 1000

            val searchTargets = listOf(
                "urn:schemas-sony-com:service:IRCC:1",
                "urn:schemas-upnp-org:device:MediaRenderer:1",
                "ssdp:all"
            )

            val group = InetAddress.getByName(ssdpAddress)

            // Send M-SEARCH packets for each target
            for (st in searchTargets) {
                val query = buildString {
                    append("M-SEARCH * HTTP/1.1\r\n")
                    append("HOST: $ssdpAddress:$ssdpPort\r\n")
                    append("MAN: \"ssdp:discover\"\r\n")
                    append("MX: 2\r\n")
                    append("ST: $st\r\n")
                    append("\r\n")
                }

                val queryBytes = query.toByteArray(Charsets.UTF_8)
                val packet = DatagramPacket(queryBytes, queryBytes.size, group, ssdpPort)
                socket.send(packet)
            }

            val buffer = ByteArray(4096)
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                try {
                    val responsePacket = DatagramPacket(buffer, buffer.size)
                    socket.receive(responsePacket)

                    val responseText = String(responsePacket.data, 0, responsePacket.length, Charsets.UTF_8)
                    val senderIp = responsePacket.address.hostAddress ?: continue

                    // Check if response is from a Sony device
                    val isSony = responseText.contains("sony", ignoreCase = true) ||
                            responseText.contains("IRCC", ignoreCase = true) ||
                            responseText.contains("BRAVIA", ignoreCase = true)

                    if (isSony && !discoveredDevices.containsKey(senderIp)) {
                        val locationHeader = extractHeader(responseText, "LOCATION")
                        val modelName = if (locationHeader != null) {
                            try {
                                URI(locationHeader).host
                            } catch (e: Exception) {
                                "BRAVIA"
                            }
                        } else "BRAVIA"

                        val device = TvDevice(
                            friendlyName = "Sony BRAVIA ($senderIp)",
                            ipAddress = senderIp,
                            psk = "1234",
                            modelName = modelName,
                            isReachable = true
                        )
                        discoveredDevices[senderIp] = device
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    // Normal timeout for polling socket receive
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                socket?.close()
            } catch (ignored: Exception) {}
            try {
                multicastLock?.let {
                    if (it.isHeld) it.release()
                }
            } catch (ignored: Exception) {}
        }

        discoveredDevices.values.toList()
    }

    private fun extractHeader(response: String, headerName: String): String? {
        val lines = response.lines()
        for (line in lines) {
            val parts = line.split(":", limit = 2)
            if (parts.size == 2 && parts[0].trim().equals(headerName, ignoreCase = true)) {
                return parts[1].trim()
            }
        }
        return null
    }
}
