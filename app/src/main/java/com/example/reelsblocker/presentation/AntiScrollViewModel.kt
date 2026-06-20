package com.example.reelsblocker.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AntiScrollViewModel: ViewModel() {
    private val _antiScrollEnabled = MutableStateFlow(false)
    val antiScrollEnabled = _antiScrollEnabled.asStateFlow()

    private val _vkScrollBlockEnabled = MutableStateFlow(false)
    val vkScrollBlockEnabled = _vkScrollBlockEnabled.asStateFlow()

    private val _youTubeScrollBlockEnabled = MutableStateFlow(false)
    val youTubeScrollBlockEnabled = _youTubeScrollBlockEnabled.asStateFlow()

    fun onAntiScrollChanged(value: Boolean) {
        _antiScrollEnabled.value = value
    }

    fun onVKScrollBlockChanged(value: Boolean) {
        _vkScrollBlockEnabled.value = value
    }

    fun onYouTubeScrollBlockChanged(value: Boolean) {
        _youTubeScrollBlockEnabled.value = value
    }
}