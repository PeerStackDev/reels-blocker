package com.example.reelsblocker.presentation

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.reelsblocker.ui.theme.BACKGROUND

@Preview(
    showSystemUi = true,
    showBackground = true,
    backgroundColor = 0xFF010D1D
)@Composable
fun PermissionDialogPreview() {
    // Оборачиваем в тему и поверхность для правильного отображения
    MaterialTheme {
        Surface {
            PermissionDialog(
                onDismiss = { /* Действие для кнопки "Позже" */ },
                onOpenSettings = { /* Действие для кнопки "Открыть настройки" */ }
            )
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Требуется разрешение") },
        text = {
            Text(text = "Для работы ReelsBlocker приложения необходимо включить сервис специальных возможностей. Без этого блокировка не будет работать.")
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