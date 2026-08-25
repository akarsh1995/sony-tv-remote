package com.sonyremote.app.data.model

/**
 * Model representing a Sony Bravia TV on the local network.
 */
data class TvDevice(
    val friendlyName: String = "Sony BRAVIA TV",
    val ipAddress: String = "",
    val psk: String = "1234",
    val macAddress: String = "",
    val modelName: String = "",
    val isReachable: Boolean = false
) {
    val isValidConfig: Boolean
        get() = ipAddress.isNotBlank() && psk.isNotBlank()
}
