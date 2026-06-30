package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

/**
 * Обработчик блокировки клипов и шопсов (AntiReels)
 * Использует GLOBAL_ACTION_BACK с ожиданием открытия окна клипа
 */
class ShortsBlocker(private val service: AccessibilityService) {

    companion object {
        private const val TAG = "ShortsBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        private var lastBlockTime = 0L
        private const val BLOCK_COOLDOWN = 0L

        // Флаг: ждём ли мы открытия клипа
        private var waitingForClipOpen = false
        private var clipOpenTime = 0L

        // Максимальное время ожидания открытия клипа (мс)
        private const val MAX_WAIT_FOR_OPEN = 1000L

        // Задержка после открытия клипа перед BACK (мс)
        private const val BACK_DELAY_AFTER_OPEN = 200L
    }

    /**
     * Обрабатывает событие клика
     */
    fun handleEvent(event: AccessibilityEvent, settings: SettingsManager): Boolean {
        if (!settings.isAntiReelsEnabled()) return false
        if (!settings.isVkBlocked()) return false
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) return false

        val text = event.text?.joinToString("") ?: ""
        val desc = event.contentDescription?.toString() ?: ""

        Log.d(TAG, "CLICK: text='$text', desc='$desc'")

        // Не блокируем клик по вкладке "Главная"
        if (desc == "Главная" || desc.startsWith("Главная ")) {
            return false
        }

        // Клик по вкладке "Клипы"
        if (text == "Клипы" || desc == "Клипы") {
            Log.d(TAG, "🚫 User clicked Clips tab!")
            startBlocking()
            return true
        }

        // Клик по клипу
        if (desc.contains("Клип") && desc != "Клипы") {
            Log.d(TAG, " User clicked clip: $desc")
            startBlocking()
            return true
        }

        // Клик по Шопсу (несколько вариантов)
        if (desc.contains("Шопс", ignoreCase = true) ||
            desc.contains("Shops", ignoreCase = true) ||
            desc.contains("Товар", ignoreCase = true) ||
            desc.contains("Магазин", ignoreCase = true)) {
            Log.d(TAG, "🚫 User clicked shops: $desc")
            startBlocking()
            return true
        }

        return false
    }

    /**
     * Запускает процесс блокировки: ждёт открытия клипа, потом BACK
     */
    private fun startBlocking() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime < BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastBlockTime = currentTime

        Log.d(TAG, "🚫 BLOCKING! Waiting for clip to open...")

        Toast.makeText(service, "🚫 Заблокировано", Toast.LENGTH_SHORT).show()

        // Устанавливаем флаг ожидания
        waitingForClipOpen = true
        clipOpenTime = currentTime

        // Страховка: если за 3 секунды клип не откроется — всё равно сделаем BACK
        Thread {
            try {
                Thread.sleep(MAX_WAIT_FOR_OPEN)
                if (waitingForClipOpen) {
                    Log.d(TAG, "⏰ Timeout! Forcing BACK...")
                    waitingForClipOpen = false
                    doBack()
                }
            } catch (e: InterruptedException) {}
        }.start()
    }

    /**
     * Вызывается из MainAccessibilityService при WINDOW_STATE_CHANGED
     */
    fun onWindowStateChanged(event: AccessibilityEvent) {
        if (!waitingForClipOpen) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != VK_PACKAGE) return

        Log.d(TAG, "📱 Window state changed: ${event.className}")

        // Клип открылся! Делаем BACK с небольшой задержкой
        waitingForClipOpen = false

        Thread {
            try {
                Thread.sleep(BACK_DELAY_AFTER_OPEN)
                Log.d(TAG, "✅ Clip opened, doing BACK...")
                doBack()
            } catch (e: InterruptedException) {}
        }.start()
    }

    /**
     * Выполняет GLOBAL_ACTION_BACK
     */
    private fun doBack() {
        val success = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        Log.d(TAG, if (success) "✅ BACK executed" else " BACK failed")
    }
}