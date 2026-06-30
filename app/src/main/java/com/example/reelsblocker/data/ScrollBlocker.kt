package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * Обработчик блокировки скролла (AntiScroll)
 */
class ScrollBlocker(private val service: AccessibilityService) {

    companion object {
        private const val TAG = "ScrollBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        private var lastScrollBlockTime = 0L
        private const val SCROLL_BLOCK_COOLDOWN = 1000L
    }

    /**
     * Обрабатывает событие скролла
     * @return true если событие обработано (заблокировано)
     */
    fun handleEvent(event: AccessibilityEvent, settings: SettingsManager): Boolean {
        if (!settings.isAntiScrollEnabled()) return false
        if (!settings.isVkScrollBlocked()) return false
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return false

        val className = event.className?.toString() ?: ""

        if (!className.contains("RecyclerView")) return false

        Log.d(TAG, "📜 SCROLL: class='$className'")

        // Проверяем, что это именно лента новостей
        service.rootInActiveWindow?.let { rootNode ->
            if (isNewsFeed(rootNode)) {
                Log.d(TAG, "🚫 News feed scroll detected! Blocking...")
                blockScroll(rootNode)
                return true
            } else {
                Log.d(TAG, "✅ Not news feed - allowing scroll")
            }
        }

        return false
    }

    /**
     * Проверяет, что на экране открыта именно лента новостей VK
     */
    private fun isNewsFeed(rootNode: AccessibilityNodeInfo): Boolean {
        val homeNodes = rootNode.findAccessibilityNodeInfosByText("Главная")
        val hasHomeTab = homeNodes.any { node ->
            val desc = node.contentDescription?.toString() ?: ""
            desc == "Главная" || desc.startsWith("Главная ") || node.isSelected
        }

        val hasFeedContent = rootNode.findAccessibilityNodeInfosByText("Что у вас нового?").isNotEmpty() ||
                rootNode.findAccessibilityNodeInfosByText("Создать").isNotEmpty()

        Log.d(TAG, "Feed check: hasHomeTab=$hasHomeTab, hasFeedContent=$hasFeedContent")

        return hasHomeTab && hasFeedContent
    }

    /**
     * Блокирует скролл - переключает на вкладку "Главная"
     */
    private fun blockScroll(rootNode: AccessibilityNodeInfo) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScrollBlockTime < SCROLL_BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastScrollBlockTime = currentTime

        Log.d(TAG, "🚫 BLOCKING SCROLL! Switching to Home...")

        Toast.makeText(service, "⏰ Лента заблокирована. Отдохните!", Toast.LENGTH_SHORT).show()

        switchToHomeTab(rootNode)
    }

    /**
     * Переключает на вкладку "Главная"
     */
    private fun switchToHomeTab(rootNode: AccessibilityNodeInfo) {
        val homeNodes = rootNode.findAccessibilityNodeInfosByText("Главная")

        if (homeNodes.isEmpty()) {
            Log.d(TAG, "❌ Home tab not found")
            return
        }

        for (node in homeNodes) {
            val desc = node.contentDescription?.toString() ?: ""

            if (desc == "Главная" || desc.startsWith("Главная ")) {
                Log.d(TAG, "✓ Found Home: desc='$desc'")

                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)

                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {}

                val clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                if (clicked) {
                    Log.d(TAG, "✅ Switched to Home")
                } else {
                    Log.d(TAG, "❌ Click failed, trying parent...")
                    node.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }

                return
            }
        }

        Log.d(TAG, "❌ Home tab not found by description")
    }
}