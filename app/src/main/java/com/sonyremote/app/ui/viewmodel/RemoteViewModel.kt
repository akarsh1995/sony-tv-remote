package com.sonyremote.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sonyremote.app.data.model.IrccCommand
import com.sonyremote.app.data.model.TvDevice
import com.sonyremote.app.data.repository.SonyTvRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SonyTvRepository(application.applicationContext)
    private val vibrator = getVibratorService(application)

    private val _uiState = MutableStateFlow(
        RemoteUiState(
            currentDevice = repository.loadSavedDevice()
        )
    )
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    private var messageClearJob: Job? = null

    init {
        // Auto-check connection if IP is already configured
        if (_uiState.value.currentDevice.isValidConfig) {
            checkConnection()
        } else {
            // Auto scan if no TV configured
            scanForTvs()
        }
    }

    fun sendCommand(command: IrccCommand) {
        performHapticFeedback()
        _uiState.update {
            it.copy(
                isSendingCommand = true,
                lastExecutedCommand = command
            )
        }

        viewModelScope.launch {
            val result = repository.sendCommand(command)
            _uiState.update {
                it.copy(
                    isSendingCommand = false,
                    isConnected = result.isSuccess,
                    statusMessage = if (result.isSuccess) {
                        "Sent: ${command.displayName}"
                    } else {
                        "Failed: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                    }
                )
            }
            scheduleMessageDismiss()
        }
    }

    fun checkConnection() {
        viewModelScope.launch {
            val device = _uiState.value.currentDevice
            if (device.isValidConfig) {
                val result = repository.testConnection(device)
                _uiState.update {
                    it.copy(
                        isConnected = result.isSuccess,
                        currentDevice = repository.loadSavedDevice(),
                        statusMessage = if (result.isSuccess) "Connected to ${device.friendlyName}" else "Cannot reach TV at ${device.ipAddress}"
                    )
                }
                scheduleMessageDismiss()
            }
        }
    }

    fun scanForTvs() {
        _uiState.update { it.copy(isScanning = true, statusMessage = "Scanning Wi-Fi for Sony TVs…") }
        viewModelScope.launch {
            val devices = repository.scanDevices()
            _uiState.update {
                it.copy(
                    isScanning = false,
                    discoveredDevices = devices,
                    statusMessage = if (devices.isEmpty()) "No Sony TVs found via SSDP" else "Found ${devices.size} Sony TV(s)"
                )
            }
            scheduleMessageDismiss()
        }
    }

    fun selectDevice(device: TvDevice) {
        repository.saveDevice(device)
        _uiState.update {
            it.copy(
                currentDevice = device,
                showSettingsDialog = false
            )
        }
        checkConnection()
    }

    fun saveManualDevice(ip: String, psk: String, name: String, mac: String) {
        val device = TvDevice(
            friendlyName = name.ifBlank { "Sony BRAVIA TV" },
            ipAddress = ip.trim(),
            psk = psk.trim().ifBlank { "1234" },
            macAddress = mac.trim()
        )
        repository.saveDevice(device)
        _uiState.update {
            it.copy(
                currentDevice = device,
                showSettingsDialog = false
            )
        }
        checkConnection()
    }

    fun sendWakeOnLan() {
        performHapticFeedback()
        viewModelScope.launch {
            val result = repository.sendWakeOnLan()
            _uiState.update {
                it.copy(
                    statusMessage = if (result.isSuccess) "Sent Wake-on-LAN Magic Packet" else "WoL Failed: ${result.exceptionOrNull()?.message}"
                )
            }
            scheduleMessageDismiss()
        }
    }

    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
        if (show && _uiState.value.discoveredDevices.isEmpty()) {
            scanForTvs()
        }
    }

    fun setShowNumpadSheet(show: Boolean) {
        _uiState.update { it.copy(showNumpadSheet = show) }
    }

    private fun scheduleMessageDismiss() {
        messageClearJob?.cancel()
        messageClearJob = viewModelScope.launch {
            delay(3500)
            _uiState.update { it.copy(statusMessage = null) }
        }
    }

    private fun performHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (ignored: Exception) {}
    }

    private fun getVibratorService(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
