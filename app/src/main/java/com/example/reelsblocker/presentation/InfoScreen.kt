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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.BASE
import com.example.reelsblocker.ui.theme.GRAY
import com.example.reelsblocker.ui.theme.SUB_BACKGROUND


@Preview(showSystemUi = true)
@Composable
fun InfoScreen() {
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
            HeaderInfo()
            BodyInfo(modifier = Modifier.weight(1f))
        }
    }
}

@Override
@Composable
fun HeaderInfo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(19.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Инфор",
                fontSize = 48.sp,
                color = Color.White
            )
            Text(
                text = "мация",
                fontSize = 48.sp,
                color = ACCENT
            )
        }

        Text(
            text = "о лучшем приложении",
            color = BASE,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BodyInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
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
                    text = "Сделано с совестью",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Описание информации",
                    fontSize = 20.sp,
                    color = GRAY
                )
            }
        }

    }
}

