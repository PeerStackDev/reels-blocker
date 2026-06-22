package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class ShortsAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "ShortsBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        private var lastBlockTime = 0L
        private const val BLOCK_COOLDOWN = 1000L

        // Уменьшенная задержка (быстрее срабатывает)
        private const val SWIPE_DELAY = 500L

        // Максимальное количество попыток свайпа
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return

        if (packageName != VK_PACKAGE) return

        val settings = SettingsManager(this)

        if (!settings.isAntiReelsEnabled()) {
            return
        }

        if (!settings.isVkBlocked()) {
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val text = event.text?.joinToString("") ?: ""
            val desc = event.contentDescription?.toString() ?: ""

            Log.d(TAG, "CLICK: text='$text', desc='$desc'")

            // Не блокируем клик по вкладке "Главная"
            if (desc == "Главная" || desc.startsWith("Главная ")) {
                return
            }

            // Клик по вкладке "Клипы"
            if (text == "Клипы" || desc == "Клипы") {
                Log.d(TAG, "🚫 User clicked Clips tab!")
                blockClips()
                return
            }

            // Клик по клипу
            if (desc.contains("Клип") && desc != "Клипы") {
                Log.d(TAG, " User clicked clip: $desc")
                blockClips()
                return
            }
        }
    }

    private fun blockClips() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime < BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastBlockTime = currentTime

        Log.d(TAG, "🚫 BLOCKING! Starting block sequence...")

        Toast.makeText(this, " VK Клипы заблокированы", Toast.LENGTH_SHORT).show()

        // Запускаем блокировку в отдельном потоке
        Thread {
            try {
                // Ждём, пока VK откроет клип
                Thread.sleep(SWIPE_DELAY)

                Log.d(TAG, "⏱️ Delay completed, attempting to close clip...")

                // Пробуем свайп с повторными попытками
                var swipeSuccess = false
                var attempts = 0

                while (!swipeSuccess && attempts < MAX_RETRY_ATTEMPTS) {
                    attempts++
                    Log.d(TAG, "🔄 Attempt $attempts/$MAX_RETRY_ATTEMPTS")

                    swipeSuccess = performBackSwipeWithRetry()

                    if (!swipeSuccess && attempts < MAX_RETRY_ATTEMPTS) {
                        Log.d(TAG, "⏳ Waiting before retry...")
                        Thread.sleep(200) // Небольшая пауза между попытками
                    }
                }

                // Если свайп не сработал — используем GLOBAL_ACTION_BACK
                if (!swipeSuccess) {
                    Log.d(TAG, " Swipe failed, using GLOBAL_ACTION_BACK as fallback")
                    performGlobalAction(GLOBAL_ACTION_BACK)
                }

            } catch (e: InterruptedException) {
                Log.e(TAG, "Block sequence interrupted", e)
            }
        }.start()
    }

    /**
     * Выполняет свайп "Назад" и возвращает true если успешно
     */
    private fun performBackSwipeWithRetry(): Boolean {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        Log.d(TAG, "Screen size: ${screenWidth}x${screenHeight}")

        // Свайп от левого края вправо
        val startX = 0f
        val startY = screenHeight / 2f
        val endX = screenWidth / 3f
        val endY = screenHeight / 2f

        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        // Используем флаг для отслеживания результата
        var completed = false

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d(TAG, "✅ Back swipe completed")
                completed = true
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.d(TAG, "❌ Back swipe cancelled")
                completed = false
            }
        }, null)

        // Ждём результат (максимум 500мс)
        var waitTime = 0
        while (!completed && waitTime < 500) {
            Thread.sleep(50)
            waitTime += 50
        }

        return completed
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ Service connected!")
    }
}