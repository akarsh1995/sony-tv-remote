package com.sonyremote.app.ui.viewmodel

import com.sonyremote.app.data.model.IrccCommand
import com.sonyremote.app.data.model.TvDevice

data class RemoteUiState(
    val currentDevice: TvDevice = TvDevice(),
    val isConnected: Boolean = false,
    val isScanning: Boolean = false,
    val isSendingCommand: Boolean = false,
    val lastExecutedCommand: IrccCommand? = null,
    val statusMessage: String? = null,
    val discoveredDevices: List<TvDevice> = emptyList(),
    val showSettingsDialog: Boolean = false,
    val showNumpadSheet: Boolean = false
)
