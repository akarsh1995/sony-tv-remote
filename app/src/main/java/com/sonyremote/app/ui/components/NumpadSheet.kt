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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonyremote.app.data.model.IrccCommand
import com.sonyremote.app.ui.theme.AccentBlue
import com.sonyremote.app.ui.theme.AccentGreen
import com.sonyremote.app.ui.theme.AccentRed
import com.sonyremote.app.ui.theme.AccentYellow
import com.sonyremote.app.ui.theme.RemoteButtonBorder
import com.sonyremote.app.ui.theme.RemoteButtonSurface
import com.sonyremote.app.ui.theme.RemoteChassisElevated
import com.sonyremote.app.ui.theme.TextPrimary
import com.sonyremote.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumpadSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onCommandClick: (IrccCommand) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RemoteChassisElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Color Buttons (Red, Green, Yellow, Blue)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorButton(color = AccentRed, label = "Red") { onCommandClick(IrccCommand.RED) }
                ColorButton(color = AccentGreen, label = "Green") { onCommandClick(IrccCommand.GREEN) }
                ColorButton(color = AccentYellow, label = "Yellow") { onCommandClick(IrccCommand.YELLOW) }
                ColorButton(color = AccentBlue, label = "Blue") { onCommandClick(IrccCommand.BLUE) }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // HDMI Quick Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "HDMI 1" to IrccCommand.HDMI1,
                    "HDMI 2" to IrccCommand.HDMI2,
                    "HDMI 3" to IrccCommand.HDMI3,
                    "HDMI 4" to IrccCommand.HDMI4
                ).forEach { (label, cmd) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RemoteButtonSurface)
                            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(12.dp))
                            .clickable { onCommandClick(cmd) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Number Pad Grid (1-9, ., 0, Prev)
            val numRows = listOf(
                listOf("1" to IrccCommand.NUM_1, "2" to IrccCommand.NUM_2, "3" to IrccCommand.NUM_3),
                listOf("4" to IrccCommand.NUM_4, "5" to IrccCommand.NUM_5, "6" to IrccCommand.NUM_6),
                listOf("7" to IrccCommand.NUM_7, "8" to IrccCommand.NUM_8, "9" to IrccCommand.NUM_9),
                listOf("." to IrccCommand.DOT, "0" to IrccCommand.NUM_0, "PREV" to IrccCommand.PREV_CHANNEL)
            )

            numRows.forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { (label, cmd) ->
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(RemoteButtonSurface)
                                .border(1.dp, RemoteButtonBorder, CircleShape)
                                .clickable { onCommandClick(cmd) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 2) 11.sp else 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ColorButton(color: Color, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
