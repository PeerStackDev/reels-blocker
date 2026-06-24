package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * Главный сервис специальных возможностей.
 * Делегирует обработку событий отдельным обработчикам.
 */
class MainAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MainService"
        private const val VK_PACKAGE = "com.vkontakte.android"
    }

    // Обработчики
    private lateinit var shortsBlocker: ShortsBlocker
    private lateinit var scrollBlocker: ScrollBlocker
    private lateinit var settings: SettingsManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ MainAccessibilityService connected!")

        // Инициализируем обработчики
        shortsBlocker = ShortsBlocker(this)
        scrollBlocker = ScrollBlocker(this)
        settings = SettingsManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return

        // Работаем только с VK
        if (packageName != VK_PACKAGE) return

        // Делегируем событие обработчикам
        // Если один обработал - выходим
        if (shortsBlocker.handleEvent(event, settings)) {
            return
        }

        if (scrollBlocker.handleEvent(event, settings)) {
            return
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
}