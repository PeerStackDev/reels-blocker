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
        private const val RUTUBE_PACKAGE = "rtb.mobile.android"

        private var lastBlockTime = 0L
        private const val BLOCK_COOLDOWN = 500L

        private var shouldBlockClipWindow = false

        private const val MAX_WAIT_FOR_OPEN = 1000L
        private const val BACK_DELAY_AFTER_OPEN = 200L

        private val RUTUBE_CLIP_KEYWORDS = listOf(
            "Short", "Shorts",
            "Шортс", "Шортсы",
            "Короткое видео",
            "Лента"
        )
    }

    fun handleEvent(event: AccessibilityEvent, settings: SettingsManager): Boolean {
        if (!settings.isAntiReelsEnabled()) return false
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) return false

        val packageName = event.packageName?.toString() ?: return false
        val text = event.text?.joinToString("") ?: ""
        val desc = event.contentDescription?.toString() ?: ""

        Log.d(TAG, "CLICK [$packageName]: text='$text', desc='$desc'")

        // === Проверяем URL в тексте клика ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            if (containsShortsUrl(text) || containsShortsUrl(desc)) {
                Log.d(TAG, "🚫 Rutube Shorts URL detected in click: text='$text', desc='$desc'")
                requestBlocking()
                return true
            }
        }

        // === VK ===
        if (packageName == VK_PACKAGE && settings.isVkBlocked()) {
            if (desc == "Клипы" || text == "Клипы") {
                Log.d(TAG, "🚫 VK: clicked Clips tab")
                requestBlocking()
                return true
            }
        }

        // === Rutube ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            val combinedText = "$text $desc"

            val isShortRelated = RUTUBE_CLIP_KEYWORDS.any { keyword ->
                combinedText.contains(keyword, ignoreCase = true)
            }

            val isNavigation = desc == "Главная" || desc == "Подписки" ||
                    desc == "Моя Rutube" || text == "Главная"

            if (isShortRelated && !isNavigation) {
                Log.d(TAG, "🚫 Rutube short-related click: text='$text', desc='$desc'")
                requestBlocking()
                return true
            }
        }

        return false
    }

    fun onWindowStateChanged(event: AccessibilityEvent, settings: SettingsManager) {
        if (!settings.isAntiReelsEnabled()) return

        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: ""
        val text = event.text?.joinToString(", ") ?: ""

        Log.d(TAG, "🔍 WINDOW_STATE_CHANGED: package='$packageName', class='$className', text='$text'")

        // === Проверяем URL в тексте события ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            if (containsShortsUrl(text)) {
                Log.d(TAG, "🚫 Rutube Shorts URL detected in window: $text")
                handleClipDetected()
                return
            }
        }

        // === VK ===
        if (packageName == VK_PACKAGE && settings.isVkBlocked()) {
            val isClipWindow = className.contains("Clip", ignoreCase = true) ||
                    className.contains("Shorts", ignoreCase = true) ||
                    className.contains("Reels", ignoreCase = true) ||
                    className.contains("VerticalVideo", ignoreCase = true)

            if (isClipWindow) {
                Log.d(TAG, "📱 VK Clip Activity detected: $className")
                handleClipDetected()
            }
        }

        // === Rutube ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            val isShortWindow = className.contains("Short", ignoreCase = true) ||
                    className.contains("VerticalVideo", ignoreCase = true) ||
                    className.contains("PlayerActivity", ignoreCase = true)

            if (isShortWindow) {
                Log.d(TAG, "📱 Rutube Short Activity detected: $className")
                handleClipDetected()
            }
        }
    }

    fun onWindowContentChanged(event: AccessibilityEvent, settings: SettingsManager) {
        if (!settings.isAntiReelsEnabled()) return

        val packageName = event.packageName?.toString() ?: return
        val text = event.text?.joinToString(", ") ?: ""

        // === Проверяем URL в тексте события ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            if (containsShortsUrl(text)) {
                Log.d(TAG, "🚫 Rutube Shorts URL detected in content change: $text")
                handleClipDetected()
                return
            }
        }

        // === VK ===
        if (packageName == VK_PACKAGE && settings.isVkBlocked()) {
            service.rootInActiveWindow?.let { rootNode ->
                if (isVKClipScreen(rootNode)) {
                    Log.d(TAG, "📱 VK Clip screen detected via content!")
                    handleClipDetected()
                }
            }
        }

        // === Rutube ===
        if (packageName == RUTUBE_PACKAGE && settings.isRuTubeBlocked()) {
            Thread {
                try {
                    Thread.sleep(800)

                    val rutubeRoot = findRutubeWindowRoot()
                    if (rutubeRoot != null) {
                        if (isRutubeShortScreen(rutubeRoot)) {
                            Log.d(TAG, "📱 Rutube Short screen detected via content!")
                            handleClipDetected()
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, "Interrupted", e)
                }
            }.start()
        }
    }

    /**
     * Проверяет, содержит ли текст URL с "shorts"
     */
    private fun containsShortsUrl(text: String): Boolean {
        if (text.isEmpty()) return false

        // Проверяем различные варианты URL с shorts
        val patterns = listOf(
            "rutube.ru/shorts/",
            "rutube.ru/short/",
            "/shorts/",
            "/short/",
            "shorts?video=",
            "short?video="
        )

        val containsUrl = patterns.any { pattern ->
            text.contains(pattern, ignoreCase = true)
        }

        if (containsUrl) {
            Log.d(TAG, "✅ Found shorts URL pattern in: ${text.take(100)}")
        }

        return containsUrl
    }

    private fun findRutubeWindowRoot(): AccessibilityNodeInfo? {
        try {
            val windows = service.windows

            for (window in windows) {
                val windowPackage = window.root?.packageName?.toString() ?: ""

                if (windowPackage == RUTUBE_PACKAGE) {
                    val root = window.root
                    if (root != null) {
                        return root
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding Rutube window", e)
        }

        return null
    }

    private fun isVKClipScreen(rootNode: AccessibilityNodeInfo): Boolean {
        val hasLike = findNodeByDesc(rootNode, "Нравится") != null

        if (hasLike) {
            Log.d(TAG, "✅ VK clip detected: like button found")
        }

        return hasLike
    }

    private fun isRutubeShortScreen(rootNode: AccessibilityNodeInfo): Boolean {
        val hasLikeByDesc = findNodeByDesc(rootNode, "Нравится") != null
        val hasLikeByText = findNodeByText(rootNode, "Нравится") != null
        val hasLike = hasLikeByDesc || hasLikeByText

        if (hasLike) {
            Log.d(TAG, "✅ Rutube short detected: likeByDesc=$hasLikeByDesc, likeByText=$hasLikeByText")
        }

        return hasLike
    }

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

    private fun findNodeByDesc(node: AccessibilityNodeInfo, desc: String): AccessibilityNodeInfo? {
        val nodeDesc = node.contentDescription?.toString() ?: ""

        if (nodeDesc.contains(desc, ignoreCase = true)) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findNodeByDesc(child, desc)
            if (result != null) return result
        }

        return null
    }

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
                    Log.d(TAG, " Timeout! Forcing BACK...")
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