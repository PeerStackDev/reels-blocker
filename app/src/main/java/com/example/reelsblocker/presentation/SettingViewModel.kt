package com.example.reelsblocker.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reelsblocker.data.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settings = SettingsManager(application)
    val dialogManager = DialogStateManager()

    // Состояния для переключателей
    private val _scheduleEnabled = MutableStateFlow(settings.isScheduleEnabled())
    val scheduleEnabled: StateFlow<Boolean> = _scheduleEnabled.asStateFlow()

    private val _screenTimeEnabled = MutableStateFlow(settings.isScreenTimeEnabled())
    val screenTimeEnabled: StateFlow<Boolean> = _screenTimeEnabled.asStateFlow()

    // Методы для переключателей
    fun onScheduleChanged(value: Boolean) {
        if (value) {
            dialogManager.showMessageBox(
                title = "Временно недоступно",
                message = "Функция блокировки по времени временно не работает."
            )
            _scheduleEnabled.value = settings.isScheduleEnabled()
        } else {
            _scheduleEnabled.value = false
            settings.setScheduleEnabled(false)
        }
    }

    fun onScreenTimeChanged(value: Boolean) {
        if (value) {
            dialogManager.showMessageBox(
                title = "Временно недоступно",
                message = "Функция блокировки по экранному времени временно не работает."
            )
            _screenTimeEnabled.value = settings.isScreenTimeEnabled()
        } else {
            _screenTimeEnabled.value = false
            settings.setScreenTimeEnabled(false)
        }
    }
}

// Фабрика для создания ViewModel
class SettingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}