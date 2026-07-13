package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MainAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MainService"
        private const val VK_PACKAGE = "com.vkontakte.android"
        private const val RUTUBE_PACKAGE = "rtb.mobile.android" // Правильный package name Rutube
    }

    private lateinit var shortsBlocker: ShortsBlocker
    private lateinit var scrollBlocker: ScrollBlocker
    private lateinit var settings: SettingsManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ MainAccessibilityService CONNECTED!")

        // Инициализируем обработчики
        shortsBlocker = ShortsBlocker(this)
        scrollBlocker = ScrollBlocker(this)
        settings = SettingsManager(this)

        Log.d(TAG, "✅ Handlers initialized")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: ""
        val eventType = event.eventType

        // Фильтруем события: работаем только с VK и Rutube
        if (packageName != VK_PACKAGE && packageName != RUTUBE_PACKAGE) {
            return
        }

        // Логируем события для отладки
        Log.d(TAG, "📩 EVENT: package='$packageName', type=$eventType")

        // 1. Обработка изменения состояния окна (открытие нового Activity)
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            shortsBlocker.onWindowStateChanged(event, settings)
        }

        // 2. Обработка изменения контента (открытие клипа внутри того же Activity)
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            shortsBlocker.onWindowContentChanged(event, settings)
        }

        // 3. Обработка кликов (перехват нажатий на кнопки клипов/шортсов)
        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (shortsBlocker.handleEvent(event, settings)) {
                return // Если клип заблокирован, дальше не идем
            }
        }

        // 4. Обработка скролла (блокировка ленты VK)
        if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (scrollBlocker.handleEvent(event, settings)) {
                return
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "❌ Service interrupted")
    }
}