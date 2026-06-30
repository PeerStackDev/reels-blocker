package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class ShortsBlocker(private val service: AccessibilityService) {

    companion object {
        private const val TAG = "ShortsBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        private var lastBlockTime = 0L
        private const val BLOCK_COOLDOWN = 1000L

        private var shouldBlockClipWindow = false

        private const val MAX_WAIT_FOR_OPEN = 1000L
        private const val BACK_DELAY_AFTER_OPEN = 200L

        private val CLIP_KEYWORDS = listOf(
            "Клип", "Клипы", "Clip", "Clips",
            "Short", "Shorts",
            "Reel", "Reels",
            "Шоп", "Shop", "Товар", "Магазин"
        )
    }

    fun handleEvent(event: AccessibilityEvent, settings: SettingsManager): Boolean {
        if (!settings.isAntiReelsEnabled()) return false
        if (!settings.isVkBlocked()) return false
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) return false

        val text = event.text?.joinToString("") ?: ""
        val desc = event.contentDescription?.toString() ?: ""

        Log.d(TAG, "CLICK: text='$text', desc='$desc'")

        if (desc == "Главная" || desc.startsWith("Главная ")) {
            return false
        }

        val combinedText = "$text $desc"
        val isClipRelated = CLIP_KEYWORDS.any { keyword ->
            combinedText.contains(keyword, ignoreCase = true)
        }

        if (isClipRelated) {
            Log.d(TAG, "🚫 Clip-related click: $combinedText")
            requestBlocking()
            return true
        }

        return false
    }

    /**
     * Обрабатывает WINDOW_STATE_CHANGED (открытие нового Activity)
     */
    fun onWindowStateChanged(event: AccessibilityEvent, settings: SettingsManager) {
        if (!settings.isAntiReelsEnabled()) return
        if (!settings.isVkBlocked()) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != VK_PACKAGE) return

        val className = event.className?.toString() ?: ""

        // Проверяем, является ли это окном клипов (отдельное Activity)
        val isClipWindow = className.contains("Clip", ignoreCase = true) ||
                className.contains("Shorts", ignoreCase = true) ||
                className.contains("Reels", ignoreCase = true) ||
                className.contains("VerticalVideo", ignoreCase = true)

        if (isClipWindow) {
            Log.d(TAG, "📱 Clip Activity detected: $className")
            handleClipDetected()
        }
    }

    /**
     * Обрабатывает WINDOW_CONTENT_CHANGED (изменение контента внутри Activity)
     * Это нужно для случая, когда клип открывается внутри MainActivity как оверлей
     */
    fun onWindowContentChanged(event: AccessibilityEvent, settings: SettingsManager) {
        if (!settings.isAntiReelsEnabled()) return
        if (!settings.isVkBlocked()) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != VK_PACKAGE) return

        // Проверяем содержимое экрана на признаки клипа
        service.rootInActiveWindow?.let { rootNode ->
            if (isClipScreen(rootNode)) {
                Log.d(TAG, "📱 Clip screen detected via content check!")
                handleClipDetected()
            }
        }
    }

    /**
     * Проверяет, открыт ли на экране клип
     * Признаки клипа: кнопки "Нравится" + "Комментарий" + "Поделиться" + "Ещё"
     * (все четыре кнопки вместе характерны для вертикальных видео)
     */
    private fun isClipScreen(rootNode: AccessibilityNodeInfo): Boolean {
        // Ищем характерные признаки клипа
        val hasLike = findNodeByText(rootNode, "Нравится") != null
        val hasComment = findNodeByText(rootNode, "Комментарий") != null ||
                findNodeByText(rootNode, "комментариев") != null
        val hasShare = findNodeByText(rootNode, "Поделиться") != null
        val hasMore = findNodeByText(rootNode, "Ещё") != null

        // Все четыре кнопки вместе — признак клипа
        val allButtonsPresent = hasLike && hasComment && hasShare && hasMore

        if (allButtonsPresent) {
            Log.d(TAG, "✅ Clip buttons found: like=$hasLike, comment=$hasComment, share=$hasShare, more=$hasMore")
        }

        return allButtonsPresent
    }

    /**
     * Ищет узел по тексту (рекурсивно)
     */
    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val nodeText = node.text?.toString() ?: ""
        val nodeDesc = node.contentDescription?.toString() ?: ""

        if (nodeText.contains(text, ignoreCase = true) ||
            nodeDesc.contains(text, ignoreCase = true)) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findNodeByText(child, text)
            if (result != null) return result
        }

        return null
    }

    /**
     * Обрабатывает обнаружение клипа (из любого источника)
     */
    private fun handleClipDetected() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime < BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastBlockTime = currentTime

        if (shouldBlockClipWindow) {
            shouldBlockClipWindow = false
            Log.d(TAG, "✅ Expected clip detected, blocking...")
        } else {
            Log.d(TAG, "🚫 Unexpected clip detected! Blocking...")
            Toast.makeText(service, "🚫 Заблокировано", Toast.LENGTH_SHORT).show()
        }

        doBackWithDelay()
    }

    private fun requestBlocking() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime < BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastBlockTime = currentTime

        Log.d(TAG, "🚫 Blocking requested!")
        Toast.makeText(service, "🚫 Заблокировано", Toast.LENGTH_SHORT).show()

        shouldBlockClipWindow = true

        Thread {
            try {
                Thread.sleep(MAX_WAIT_FOR_OPEN)
                if (shouldBlockClipWindow) {
                    Log.d(TAG, "⏰ Timeout! Forcing BACK...")
                    shouldBlockClipWindow = false
                    doBack()
                }
            } catch (e: InterruptedException) {}
        }.start()
    }

    private fun doBackWithDelay() {
        Thread {
            try {
                Thread.sleep(BACK_DELAY_AFTER_OPEN)
                doBack()
            } catch (e: InterruptedException) {}
        }.start()
    }

    private fun doBack() {
        val success = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        Log.d(TAG, if (success) "✅ BACK executed" else "❌ BACK failed")
    }
}