package com.example.reelsblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.reelsblocker.data.AccessibilityHelper
import com.example.reelsblocker.presentation.AntiReelsScreen
import com.example.reelsblocker.presentation.AntiScrollScreen
import com.example.reelsblocker.presentation.MainScreen
import com.example.reelsblocker.ui.theme.ReelsBlockerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReelsBlockerTheme {
                MainApp()
            }
        }
    }
}

// Экраны приложения
enum class Screen {
    MAIN,
    ANTI_REELS,
    ANTI_SCROLL
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Проверяем права при запуске
    LaunchedEffect(Unit) {
        if (!AccessibilityHelper.isAccessibilityServiceEnabled(context)) {
            showPermissionDialog = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.MAIN -> MainScreen(
                onNavigateToAntiReels = { currentScreen = Screen.ANTI_REELS },
                onNavigateToAntiScroll = { currentScreen = Screen.ANTI_SCROLL }
            )
            Screen.ANTI_REELS -> AntiReelsScreen(
                onBack = { currentScreen = Screen.MAIN }
            )
            Screen.ANTI_SCROLL -> AntiScrollScreen(
                onBack = { currentScreen = Screen.MAIN }
            )
        }
    }

    // Диалог с просьбой выдать права
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onOpenSettings = {
                AccessibilityHelper.openAccessibilitySettings(context)
                showPermissionDialog = false
            }
        )
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "⚠️ Требуется разрешение") },
        text = {
            Text(text = "Для работы приложения необходимо включить сервис специальных возможностей. Без этого блокировка не будет работать.")
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(text = "Открыть настройки")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Позже")
            }
        }
    )
}