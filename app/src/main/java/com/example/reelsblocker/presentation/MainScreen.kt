package com.example.reelsblocker.presentation


import FooterNav
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.reelsblocker.R
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.BASE
import com.example.reelsblocker.ui.theme.GRAY
import com.example.reelsblocker.ui.theme.SUB_BACKGROUND
import com.example.reelsblocker.ui.theme.accentStyle
import com.example.reelsblocker.ui.theme.baseStyle


@Preview(showSystemUi = true)
@Composable
fun MainScreen() {
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
            HeaderScr()
            BodyScr(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun HeaderScr() {
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
                text = "Reels",
                fontSize = 48.sp,
                color = Color.White
            )
            Text(
                text = "Blocker",
                fontSize = 48.sp,
                color = ACCENT
            )
        }

        Text(
            text = "Время это ресурс - нужно ценить его",
            color = BASE,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Карточка "Сегодня сэкономлено"
        Row(
            modifier = Modifier
                .background(
                    color = SUB_BACKGROUND,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Сегодня сэкономлено",
                    style = baseStyle,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "X минут(ы)",
                    style = accentStyle,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Это время для вас",
                    style = baseStyle,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.main_screen_logo),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}

@Composable
fun BodyScr(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Карточка "AntiReels"
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
            Image(
                painter = painterResource(id = R.drawable.anti_reels_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "AntiReels",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Выключено",
                    fontSize = 20.sp,
                    color = GRAY
                )
                Text(
                    text = "Блокировка коротких видео",
                    fontSize = 15.sp,
                    style = baseStyle
                )
            }

            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff1C2736)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.strelka),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // Карточка "AntiScroll"
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
            Image(
                painter = painterResource(id = R.drawable.anti_scroll_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "AntiScroll",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Выключено",
                    fontSize = 20.sp,
                    color = GRAY
                )
                Text(
                    text = "Блокировка бесконечной прокрутки ленты",
                    maxLines = 2,
                    fontSize = 15.sp,
                    style = baseStyle
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff1C2736)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.strelka),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}



// Попытка сделать навигатор, пока неудачно
