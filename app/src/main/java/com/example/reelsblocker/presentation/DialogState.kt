package com.example.reelsblocker.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DialogStateManager {
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    fun showMessageBox(title: String, message: String) {
        _dialogTitle.value = title
        _dialogMessage.value = message
        _showDialog.value = true
    }

    fun onConfirm() {
        _showDialog.value = false
    }

    fun onDismiss() {
        _showDialog.value = false
    }
}