package com.sonyremote.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonyremote.app.data.model.IrccCommand
import com.sonyremote.app.ui.components.ConnectionBar
import com.sonyremote.app.ui.components.DPadControl
import com.sonyremote.app.ui.components.NumpadSheet
import com.sonyremote.app.ui.components.SettingsDialog
import com.sonyremote.app.ui.components.VolumeChannelSection
import com.sonyremote.app.ui.theme.AccentBlue
import com.sonyremote.app.ui.theme.AccentOrange
import com.sonyremote.app.ui.theme.AccentRed
import com.sonyremote.app.ui.theme.RemoteButtonBorder
import com.sonyremote.app.ui.theme.RemoteButtonSurface
import com.sonyremote.app.ui.theme.RemoteChassisDark
import com.sonyremote.app.ui.theme.RemoteChassisElevated
import com.sonyremote.app.ui.theme.TextPrimary
import com.sonyremote.app.ui.theme.TextSecondary
import com.sonyremote.app.ui.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteScreen(
    viewModel: RemoteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val numpadSheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = RemoteChassisDark
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top Connection Bar
                ConnectionBar(
                    tvName = uiState.currentDevice.friendlyName,
                    tvIp = uiState.currentDevice.ipAddress,
                    isConnected = uiState.isConnected,
                    isScanning = uiState.isScanning,
                    onSettingsClick = { viewModel.setShowSettingsDialog(true) },
                    onReconnectClick = { viewModel.checkConnection() },
                    onPowerToggle = { viewModel.sendCommand(IrccCommand.POWER) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Status message chip if active
                AnimatedVisibility(
                    visible = uiState.statusMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RemoteButtonSurface)
                            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = uiState.statusMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = AccentBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Top Quick Buttons Row (Input, Display Info, Subtitles, Options)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickIconButton(icon = Icons.Default.Input, label = "INPUT") {
                        viewModel.sendCommand(IrccCommand.INPUT)
                    }
                    QuickIconButton(icon = Icons.Default.Subtitles, label = "SUB") {
                        viewModel.sendCommand(IrccCommand.SUBTITLE)
                    }
                    QuickIconButton(icon = Icons.Default.Menu, label = "OPTIONS") {
                        viewModel.sendCommand(IrccCommand.OPTIONS)
                    }
                    QuickIconButton(icon = Icons.Default.Dialpad, label = "123") {
                        viewModel.setShowNumpadSheet(true)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. 5-Way D-Pad
                DPadControl(
                    onUpClick = { viewModel.sendCommand(IrccCommand.UP) },
                    onDownClick = { viewModel.sendCommand(IrccCommand.DOWN) },
                    onLeftClick = { viewModel.sendCommand(IrccCommand.LEFT) },
                    onRightClick = { viewModel.sendCommand(IrccCommand.RIGHT) },
                    onEnterClick = { viewModel.sendCommand(IrccCommand.ENTER) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Navigation Row (BACK and HOME)
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PillActionButton(
                        icon = Icons.Default.ArrowBack,
                        label = "BACK"
                    ) {
                        viewModel.sendCommand(IrccCommand.BACK)
                    }

                    PillActionButton(
                        icon = Icons.Default.Home,
                        label = "HOME",
                        accentColor = AccentBlue
                    ) {
                        viewModel.sendCommand(IrccCommand.HOME)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Volume & Channel Rockers
                VolumeChannelSection(
                    onVolumeUp = { viewModel.sendCommand(IrccCommand.VOLUME_UP) },
                    onVolumeDown = { viewModel.sendCommand(IrccCommand.VOLUME_DOWN) },
                    onMuteClick = { viewModel.sendCommand(IrccCommand.MUTE) },
                    onChannelUp = { viewModel.sendCommand(IrccCommand.CHANNEL_UP) },
                    onChannelDown = { viewModel.sendCommand(IrccCommand.CHANNEL_DOWN) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 7. App Shortcuts (Netflix, YouTube, Prime)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppButton(
                        label = "NETFLIX",
                        textColor = AccentRed,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand(IrccCommand.NETFLIX)
                    }

                    AppButton(
                        label = "YouTube",
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand(IrccCommand.YOUTUBE)
                    }

                    AppButton(
                        label = "prime",
                        textColor = AccentBlue,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand(IrccCommand.PRIME_VIDEO)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 8. Media Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaIconButton(icon = Icons.Default.FastRewind) {
                        viewModel.sendCommand(IrccCommand.REWIND)
                    }
                    MediaIconButton(icon = Icons.Default.PlayArrow, isProminent = true) {
                        viewModel.sendCommand(IrccCommand.PLAY)
                    }
                    MediaIconButton(icon = Icons.Default.FastForward) {
                        viewModel.sendCommand(IrccCommand.FORWARD)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Modals
            if (uiState.showSettingsDialog) {
                SettingsDialog(
                    currentDevice = uiState.currentDevice,
                    discoveredDevices = uiState.discoveredDevices,
                    isScanning = uiState.isScanning,
                    onScanClick = { viewModel.scanForTvs() },
                    onDeviceSelect = { viewModel.selectDevice(it) },
                    onSaveManual = { ip, psk, name, mac ->
                        viewModel.saveManualDevice(ip, psk, name, mac)
                    },
                    onSendWol = { viewModel.sendWakeOnLan() },
                    onDismiss = { viewModel.setShowSettingsDialog(false) }
                )
            }

            if (uiState.showNumpadSheet) {
                NumpadSheet(
                    sheetState = numpadSheetState,
                    onCommandClick = { viewModel.sendCommand(it) },
                    onDismiss = { viewModel.setShowNumpadSheet(false) }
                )
            }
        }
    }
}

@Composable
fun QuickIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(RemoteButtonSurface)
                .border(1.dp, RemoteButtonBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondary
        )
    }
}

@Composable
fun PillActionButton(
    icon: ImageVector,
    label: String,
    accentColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 110.dp, height = 48.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(RemoteButtonSurface)
            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = accentColor
            )
        }
    }
}

@Composable
fun AppButton(
    label: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(RemoteButtonSurface)
            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun MediaIconButton(
    icon: ImageVector,
    isProminent: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isProminent) 54.dp else 44.dp)
            .clip(CircleShape)
            .background(if (isProminent) AccentBlue else RemoteButtonSurface)
            .border(
                1.dp,
                if (isProminent) AccentBlue else RemoteButtonBorder,
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isProminent) Color.White else TextPrimary,
            modifier = Modifier.size(if (isProminent) 28.dp else 20.dp)
        )
    }
}
