package com.example.reelsblocker.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reelsblocker.R
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.GRAY
import com.example.reelsblocker.ui.theme.SUB_BACKGROUND
import com.example.reelsblocker.ui.theme.accentStyle
import com.example.reelsblocker.ui.theme.baseStyle

import com.example.reelsblocker.presentation.AntiReelsViewModel

//@Preview(showSystemUi = true)
@Composable
fun AntiReelsScreen(
    onBack: () -> Unit
)
{
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
            HeaderARScreen(
                onBack = onBack
            )
            BodyARScreen(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun HeaderARScreen(
    onBack: () -> Unit,
    viewModel: AntiReelsViewModel = viewModel()
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Кнопка возврата в Main
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff1C2736)
            ),
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.strelka),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }

        Text(
            text = "AntiReels",
            style = accentStyle,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
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
    )
    {
        val antiReelsEnabled by viewModel.antiReelsEnabled.collectAsState()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Статус",
                fontSize = 20.sp,
                color = GRAY
            )
            Crossfade(
                targetState = antiReelsEnabled,
                label = "status"
            ) { enabled ->
                Text(
                    text = if (enabled) "Включено" else "Выключено",
                    fontSize = 32.sp,
                    color = if (enabled) ACCENT else GRAY
                )
            }
            Crossfade(
                targetState = antiReelsEnabled,
                label = "status"
            ) { enabled ->
                Text(
                    text = if (enabled) "Короткие видео заблокированы"
                    else "Короткие видео не заблокированы",
                    fontSize = 15.sp,
                    style = baseStyle
                )
            }

        }

        val antiReelsBlocked by viewModel.antiReelsEnabled.collectAsState()

        Switch(
            checked = antiReelsBlocked,
            onCheckedChange = viewModel::onAntiReelsChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ACCENT,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = GRAY
            )
        )
    }
}

@Composable
fun BodyARScreen(
    modifier: Modifier = Modifier,
    viewModel: AntiReelsViewModel = viewModel()
)
{
    Text(
        text = "Блокировать короткие видео в:",
        style = baseStyle,
        fontSize = 15.sp,
        textAlign = TextAlign.Left,
        modifier = Modifier.fillMaxWidth()
    )
    Column(modifier = Modifier
        .background(
            color = SUB_BACKGROUND,
            shape = RoundedCornerShape(16.dp)
        )
        .fillMaxWidth()
        .padding(vertical = 30.dp, horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp))
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painter = painterResource(id = R.drawable.vk_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "VK",
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "ВК Клипы",
                    fontSize = 15.sp,
                    color = GRAY
                )
            }
            val vkBlocked by viewModel.vkBlockEnabled.collectAsState()
            Switch(
                checked = vkBlocked,
                onCheckedChange = viewModel::onVKBlockChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ACCENT,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GRAY
                )
            )
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painter = painterResource(id = R.drawable.youtube_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "YouTube",
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "YouTube Shorts",
                    fontSize = 15.sp,
                    color = GRAY
                )
            }
            val ytBlocked by viewModel.youTubeBlockEnabled.collectAsState()
            Switch(
                checked = ytBlocked,
                onCheckedChange = viewModel::onYouTubeBlockChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ACCENT,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GRAY
                )
            )
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painter = painterResource(id = R.drawable.rutube_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "RuTube",
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "Короткие видео",
                    fontSize = 15.sp,
                    color = GRAY
                )
            }
            val rtBlocked by viewModel.ruTubeBlockEnabled.collectAsState()
            Switch(
                checked = rtBlocked,
                onCheckedChange = viewModel::onRuTubeBlockChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ACCENT,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GRAY
                )
            )
        }


    }

}