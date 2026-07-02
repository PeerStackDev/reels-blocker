package com.example.reelsblocker.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reelsblocker.data.SettingsManager
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.GRAY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AntiScrollViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsManager(application)

    // Общий переключатель AntiScroll
    private val _antiScrollEnabled = MutableStateFlow(settings.isAntiScrollEnabled())
    val antiScrollEnabled: StateFlow<Boolean> = _antiScrollEnabled.asStateFlow()

    // Блокировка скролла в VK
    private val _vkScrollBlockEnabled = MutableStateFlow(settings.isVkScrollBlocked())
    val vkScrollBlockEnabled: StateFlow<Boolean> = _vkScrollBlockEnabled.asStateFlow()

    // Блокировка скролла в YouTube
    private val _youTubeScrollBlockEnabled = MutableStateFlow(settings.isYouTubeScrollBlocked())
    val youTubeScrollBlockEnabled: StateFlow<Boolean> = _youTubeScrollBlockEnabled.asStateFlow()

    // Цвет для демонстрации
    private val _backgroundColor = MutableStateFlow(Color(0xFF1C2736))
    val backgroundColor: StateFlow<Color> = _backgroundColor.asStateFlow()

    fun onAntiScrollChanged(value: Boolean) {
        _antiScrollEnabled.value = value
        settings.setAntiScrollEnabled(value)

        _backgroundColor.value = if (value) {
            ACCENT
        } else {
            GRAY
        }
    }

    fun onVKScrollBlockChanged(value: Boolean) {
        _vkScrollBlockEnabled.value = value
        settings.setVkScrollBlocked(value)
    }

    fun onYouTubeScrollBlockChanged(value: Boolean) {
        _youTubeScrollBlockEnabled.value = value
        settings.setYouTubeScrollBlocked(value)
    }
}

class AntiScrollViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AntiScrollViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AntiScrollViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}