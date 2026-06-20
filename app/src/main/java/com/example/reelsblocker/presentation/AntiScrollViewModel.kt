package com.example.reelsblocker.presentation
import android.util.Log
import androidx.compose.ui.graphics.Color

import androidx.lifecycle.ViewModel
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.GRAY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AntiScrollViewModel: ViewModel() {
    private val _antiScrollEnabled = MutableStateFlow(false)
    val antiScrollEnabled = _antiScrollEnabled.asStateFlow()

    private val _vkScrollBlockEnabled = MutableStateFlow(false)
    val vkScrollBlockEnabled = _vkScrollBlockEnabled.asStateFlow()

    private val _youTubeScrollBlockEnabled = MutableStateFlow(false)
    val youTubeScrollBlockEnabled = _youTubeScrollBlockEnabled.asStateFlow()

    private val _backgroundColor = MutableStateFlow(Color(0xFF1C2736))
    val backgroundColor: StateFlow<Color> = _backgroundColor.asStateFlow()
    fun onAntiScrollChanged(value: Boolean) {
        _antiScrollEnabled.value = value
        // Изменяем цвет ВКЛЮЧЕНО, для демострации, потом здесь
        // наверное нужно обернуть все в TRY CATCH
        _backgroundColor.value = if (value) {
            ACCENT // Зеленый
        } else {
            GRAY // Исходный
        }

    }

    fun onVKScrollBlockChanged(value: Boolean) {
        _vkScrollBlockEnabled.value = value
    }

    fun onYouTubeScrollBlockChanged(value: Boolean) {
        _youTubeScrollBlockEnabled.value = value
    }
}