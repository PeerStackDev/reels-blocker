package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MainAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MainService"
        private const val VK_PACKAGE = "com.vkontakte.android"
    }

    private lateinit var shortsBlocker: ShortsBlocker
    private lateinit var scrollBlocker: ScrollBlocker
    private lateinit var settings: SettingsManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ MainAccessibilityService connected!")

        shortsBlocker = ShortsBlocker(this)
        scrollBlocker = ScrollBlocker(this)
        settings = SettingsManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return

        if (packageName != VK_PACKAGE) return

        // === Передаём WINDOW_STATE_CHANGED в ShortsBlocker ===
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            shortsBlocker.onWindowStateChanged(event, settings)
        }

        // === НОВОЕ: Передаём WINDOW_CONTENT_CHANGED в ShortsBlocker ===
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            shortsBlocker.onWindowContentChanged(event, settings)
        }

        // Делегируем клики
        if (shortsBlocker.handleEvent(event, settings)) {
            return
        }

        // Делегируем скролл
        if (scrollBlocker.handleEvent(event, settings)) {
            return
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
}