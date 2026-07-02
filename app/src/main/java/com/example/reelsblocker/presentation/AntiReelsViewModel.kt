package com.example.reelsblocker.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.reelsblocker.data.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AntiReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsManager(application)

    private val _antiReelsEnabled = MutableStateFlow(settings.isAntiReelsEnabled())
    val antiReelsEnabled: StateFlow<Boolean> = _antiReelsEnabled.asStateFlow()

    private val _vkBlockEnabled = MutableStateFlow(settings.isVkBlocked())
    val vkBlockEnabled: StateFlow<Boolean> = _vkBlockEnabled.asStateFlow()

    private val _youTubeBlockEnabled = MutableStateFlow(settings.isYouTubeBlocked())
    val youTubeBlockEnabled: StateFlow<Boolean> = _youTubeBlockEnabled.asStateFlow()

    private val _ruTubeBlockEnabled = MutableStateFlow(settings.isRuTubeBlocked())
    val ruTubeBlockEnabled: StateFlow<Boolean> = _ruTubeBlockEnabled.asStateFlow()

    val dialogManager = DialogStateManager()

    fun onAntiReelsChanged(value: Boolean) {
        _antiReelsEnabled.value = value
        settings.setAntiReelsEnabled(value)
    }

    fun onVKBlockChanged(value: Boolean) {
        _vkBlockEnabled.value = value
        settings.setVkBlocked(value)
    }

    fun onYouTubeBlockChanged(value: Boolean) {
        if (value) {
            dialogManager.showMessageBox(
                title = "Временно недоступно",
                message = "Блокировка YouTube временно не работает."
            )
            _youTubeBlockEnabled.value = settings.isYouTubeBlocked()
        } else {
            _youTubeBlockEnabled.value = false
            settings.setYouTubeBlocked(false)
        }
    }

    fun onRuTubeBlockChanged(value: Boolean) {
        if (value) {
            dialogManager.showMessageBox(
                title = "Временно недоступно",
                message = "Блокировка RuTube временно не работает."
            )
            _youTubeBlockEnabled.value = settings.isYouTubeBlocked()
        } else {
            _youTubeBlockEnabled.value = false
            settings.setYouTubeBlocked(false)
        }
    }

}

