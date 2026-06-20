package com.example.reelsblocker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AntiReelsViewModel: ViewModel() {
    private val _antiReelsEnabled = MutableStateFlow(false)
    val antiReelsEnabled = _antiReelsEnabled.asStateFlow()

    private val _vkBlockEnabled = MutableStateFlow(false)
    val vkBlockEnabled = _vkBlockEnabled.asStateFlow()

    private val _youTubeBlockEnabled = MutableStateFlow(false)
    val youTubeBlockEnabled = _youTubeBlockEnabled.asStateFlow()

    private val _ruTubeBlockEnabled = MutableStateFlow(false)
    val ruTubeBlockEnabled = _ruTubeBlockEnabled.asStateFlow()

    fun onAntiReelsChanged(value: Boolean) {
        _antiReelsEnabled.value = value
    }

    fun onVKBlockChanged(value: Boolean) {
        _vkBlockEnabled.value = value
    }

    fun onYouTubeBlockChanged(value: Boolean) {
        _youTubeBlockEnabled.value = value
    }

    fun onRuTubeBlockChanged(value: Boolean) {
        _ruTubeBlockEnabled.value = value
    }


}