package com.example.reelsblocker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.BASE
import com.example.reelsblocker.ui.theme.GRAY
import com.example.reelsblocker.ui.theme.SUB_BACKGROUND

@Composable
fun SettingScreen(
    viewModel: SettingViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010D1D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 10.dp, end = 10.dp, bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeaderSetting()
            BodySetting(modifier = Modifier.weight(1f),viewModel )
        }
    }
}

@Composable
fun HeaderSetting() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(19.dp)
    ) {
        // Логотип "ReelsBlocker"
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Наст",
                fontSize = 48.sp,
                color = Color.White
            )
            Text(
                text = "ройки",
                fontSize = 48.sp,
                color = ACCENT
            )
        }

        Text(
            text = "Разные режимы работы",
            color = BASE,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BodySetting(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel
) {
    val showDialog by viewModel.dialogManager.showDialog.collectAsState()
    val dialogTitle by viewModel.dialogManager.dialogTitle.collectAsState()
    val dialogMessage by viewModel.dialogManager.dialogMessage.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // По времени
        Row(
            modifier = Modifier
                .background(
                    color = SUB_BACKGROUND,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .padding(vertical = 30.dp, horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "По времени",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Включение блокировки по заданному расписанию",
                    fontSize = 20.sp,
                    color = GRAY
                )
            }
            val scheduleBlocked by viewModel.scheduleEnabled.collectAsState()
            Switch(
                checked = scheduleBlocked,
                onCheckedChange = viewModel::onScheduleChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ACCENT,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GRAY
                )
            )
        }

        // Экранное время
        Row(
            modifier = Modifier
                .background(
                    color = SUB_BACKGROUND,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .padding(vertical = 30.dp, horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Экранное время",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Включение блокировки по истечению таймера",
                    fontSize = 20.sp,
                    color = GRAY
                )
            }
            val screenTimeBlocked by viewModel.screenTimeEnabled.collectAsState()
            Switch(
                checked = screenTimeBlocked,
                onCheckedChange = viewModel::onScreenTimeChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ACCENT,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GRAY
                )
            )
        }
    }

    if (showDialog) {
        MessageBox(
            title = dialogTitle,
            message = dialogMessage,
            onConfirm = { viewModel.dialogManager.onConfirm() },
            onDismiss = { viewModel.dialogManager.onDismiss() }
        )
    }
}