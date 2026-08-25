package com.sonyremote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonyremote.app.ui.theme.AccentRed
import com.sonyremote.app.ui.theme.RemoteButtonBorder
import com.sonyremote.app.ui.theme.RemoteButtonSurface
import com.sonyremote.app.ui.theme.TextPrimary
import com.sonyremote.app.ui.theme.TextSecondary

@Composable
fun VolumeChannelSection(
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onMuteClick: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Volume Rocker Pill
        RockerControl(
            label = "VOL",
            onPlusClick = onVolumeUp,
            onMinusClick = onVolumeDown
        )

        // Center Mute / Middle Control
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(RemoteButtonSurface)
                .border(1.dp, RemoteButtonBorder, CircleShape)
                .clickable { onMuteClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeMute,
                    contentDescription = "Mute",
                    tint = AccentRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "MUTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextSecondary
                )
            }
        }

        // Channel Rocker Pill
        RockerControl(
            label = "CH",
            onPlusClick = onChannelUp,
            onMinusClick = onChannelDown
        )
    }
}

@Composable
fun RockerControl(
    label: String,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rockerWidth = 64.dp
    val rockerHeight = 140.dp
    val shape = RoundedCornerShape(32.dp)

    Column(
        modifier = modifier
            .size(width = rockerWidth, height = rockerHeight)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(RemoteButtonSurface)
            .border(1.5.dp, RemoteButtonBorder, shape),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Plus Button (Top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onPlusClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "$label Plus",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Center Label Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = TextSecondary
            )
        }

        // Minus Button (Bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onMinusClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "$label Minus",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
