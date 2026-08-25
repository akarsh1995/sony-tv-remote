package com.sonyremote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonyremote.app.ui.theme.AccentBlue
import com.sonyremote.app.ui.theme.RemoteButtonBorder
import com.sonyremote.app.ui.theme.RemoteButtonSurface
import com.sonyremote.app.ui.theme.RemoteChassisElevated
import com.sonyremote.app.ui.theme.TextPrimary

@Composable
fun DPadControl(
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dpadSize = 220.dp
    val okSize = 78.dp

    Box(
        modifier = modifier
            .size(dpadSize)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(RemoteChassisElevated, RemoteButtonSurface)
                )
            )
            .border(1.5.dp, RemoteButtonBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // UP Button
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 55.dp)
                .align(Alignment.TopCenter)
                .offset(y = 8.dp)
                .clip(CircleShape)
                .clickable { onUpClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Up",
                tint = TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        // DOWN Button
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 55.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-8).dp)
                .clip(CircleShape)
                .clickable { onDownClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                tint = TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        // LEFT Button
        Box(
            modifier = Modifier
                .size(width = 55.dp, height = 90.dp)
                .align(Alignment.CenterStart)
                .offset(x = 8.dp)
                .clip(CircleShape)
                .clickable { onLeftClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Left",
                tint = TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        // RIGHT Button
        Box(
            modifier = Modifier
                .size(width = 55.dp, height = 90.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-8).dp)
                .clip(CircleShape)
                .clickable { onRightClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Right",
                tint = TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        // CENTER OK / ENTER Button
        Box(
            modifier = Modifier
                .size(okSize)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(RemoteButtonSurface, Color(0xFF1E212B))
                    )
                )
                .border(2.dp, AccentBlue.copy(alpha = 0.6f), CircleShape)
                .clickable { onEnterClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                ),
                color = TextPrimary
            )
        }
    }
}
