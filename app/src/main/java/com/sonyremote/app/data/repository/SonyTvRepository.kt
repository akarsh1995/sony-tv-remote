package com.sonyremote.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.sonyremote.app.data.model.IrccCommand
import com.sonyremote.app.data.model.TvDevice
import com.sonyremote.app.data.network.SonyTvClient
import com.sonyremote.app.data.network.SsdpDiscoveryManager
import com.sonyremote.app.data.network.WakeOnLanManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository coordinating Sony TV configuration, persistence, discovery, and IRCC commands.
 */
class SonyTvRepository(
    private val context: Context,
    private val tvClient: SonyTvClient = SonyTvClient(),
    private val discoveryManager: SsdpDiscoveryManager = SsdpDiscoveryManager(context)
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sony_remote_prefs", Context.MODE_PRIVATE)

    private val _currentDevice = MutableStateFlow(loadSavedDevice())
    val currentDevice: StateFlow<TvDevice> = _currentDevice.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun loadSavedDevice(): TvDevice {
        val ip = prefs.getString("KEY_TV_IP", "") ?: ""
        val psk = prefs.getString("KEY_TV_PSK", "1234") ?: "1234"
        val name = prefs.getString("KEY_TV_NAME", "Sony BRAVIA TV") ?: "Sony BRAVIA TV"
        val mac = prefs.getString("KEY_TV_MAC", "") ?: ""
        val model = prefs.getString("KEY_TV_MODEL", "") ?: ""
        return TvDevice(
            friendlyName = name,
            ipAddress = ip,
            psk = psk,
            macAddress = mac,
            modelName = model,
            isReachable = false
        )
    }

    fun saveDevice(device: TvDevice) {
        prefs.edit()
            .putString("KEY_TV_IP", device.ipAddress)
            .putString("KEY_TV_PSK", device.psk)
            .putString("KEY_TV_NAME", device.friendlyName)
            .putString("KEY_TV_MAC", device.macAddress)
            .putString("KEY_TV_MODEL", device.modelName)
            .apply()
        _currentDevice.value = device
    }

    suspend fun testConnection(device: TvDevice): Result<Boolean> {
        val result = tvClient.checkPowerStatus(device.ipAddress, device.psk)
        if (result.isSuccess) {
            _isConnected.value = true
            // Try fetching MAC and model if missing
            if (device.macAddress.isBlank()) {
                tvClient.getSystemInfo(device.ipAddress, device.psk).onSuccess { info ->
                    val updated = device.copy(
                        macAddress = info["macAddr"] ?: device.macAddress,
                        modelName = info["model"] ?: device.modelName,
                        isReachable = true
                    )
                    saveDevice(updated)
                }
            }
        } else {
            _isConnected.value = false
        }
        return result
    }

    suspend fun sendCommand(command: IrccCommand): Result<Unit> {
        val device = _currentDevice.value
        if (!device.isValidConfig) {
            return Result.failure(IllegalStateException("TV IP Address or PSK is not configured."))
        }
        val result = tvClient.sendIrccCommand(device.ipAddress, device.psk, command)
        if (result.isSuccess) {
            _isConnected.value = true
        }
        return result
    }

    suspend fun sendWakeOnLan(): Result<Unit> {
        val mac = _currentDevice.value.macAddress
        if (mac.isBlank()) {
            return Result.failure(IllegalStateException("No MAC address configured for Wake-on-LAN."))
        }
        return WakeOnLanManager.sendMagicPacket(mac)
    }

    suspend fun scanDevices(): List<TvDevice> {
        return discoveryManager.discoverDevices()
    }
}
