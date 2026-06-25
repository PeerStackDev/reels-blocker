package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

/**
 * Обработчик блокировки клипов (AntiReels)
 */
class ShortsBlocker(private val service: AccessibilityService) {

    companion object {
        private const val TAG = "ShortsBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        private var lastBlockTime = 0L
        private const val BLOCK_COOLDOWN = 2000L
        private const val SWIPE_DELAY = 500L
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    /**
     * Обрабатывает событие клика
     * @return true если событие обработано (заблокировано)
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
            blockClips()
            return true
        }

        // Клик по клипу
        if (desc.contains("Клип") && desc != "Клипы") {
            Log.d(TAG, "🚫 User clicked clip: $desc")
            blockClips()
            return true
        }

        return false
    }

    private fun blockClips() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime < BLOCK_COOLDOWN) {
            return
        }
        lastBlockTime = currentTime

        Log.d(TAG, " BLOCKING CLIPS! Starting block sequence...")

        Toast.makeText(service, "🚫 VK Клипы заблокированы", Toast.LENGTH_SHORT).show()

        Thread {
            try {
                Thread.sleep(SWIPE_DELAY)

                var swipeSuccess = false
                var attempts = 0

                while (!swipeSuccess && attempts < MAX_RETRY_ATTEMPTS) {
                    attempts++
                    Log.d(TAG, " Attempt $attempts/$MAX_RETRY_ATTEMPTS")

                    swipeSuccess = performBackSwipeWithRetry()

                    if (!swipeSuccess && attempts < MAX_RETRY_ATTEMPTS) {
                        Thread.sleep(200)
                    }
                }

                if (!swipeSuccess) {
                    Log.d(TAG, "❌ Swipe failed, using GLOBAL_ACTION_BACK")
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                }

            } catch (e: InterruptedException) {
                Log.e(TAG, "Block sequence interrupted", e)
            }
        }.start()
    }

    private fun performBackSwipeWithRetry(): Boolean {
        val displayMetrics = service.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

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

        var completed = false

        service.dispatchGesture(gesture, object : android.accessibilityservice.AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d(TAG, "✅ Back swipe completed")
                completed = true
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.d(TAG, "❌ Back swipe cancelled")
                completed = false
            }
        }, null)

        var waitTime = 0
        while (!completed && waitTime < 500) {
            Thread.sleep(50)
            waitTime += 50
        }

        return completed
    }
}