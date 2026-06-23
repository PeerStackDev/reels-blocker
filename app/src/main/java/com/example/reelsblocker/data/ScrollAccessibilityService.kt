package com.example.reelsblocker.data

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class ScrollAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "ScrollBlocker"
        private const val VK_PACKAGE = "com.vkontakte.android"

        // Кулдаун между блокировками скролла (3 секунды)
        private var lastScrollBlockTime = 0L
        private const val SCROLL_BLOCK_COOLDOWN = 3000L
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return

        // Работаем только с VK
        if (packageName != VK_PACKAGE) return

        val settings = SettingsManager(this)

        // Проверяем настройки
        if (!settings.isAntiScrollEnabled()) return
        if (!settings.isVkScrollBlocked()) return

        // === Блокируем скролл ТОЛЬКО в ленте новостей ===
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val className = event.className?.toString() ?: ""

            // Сначала проверяем, что это список (RecyclerView)
            if (!className.contains("RecyclerView")) return

            Log.d(TAG, "📜 SCROLL: class='$className'")

            // ГЛАВНОЕ: Проверяем, что это именно лента новостей, а не мессенджер или друзья
            rootInActiveWindow?.let { rootNode ->
                if (isNewsFeed(rootNode)) {
                    Log.d(TAG, "🚫 News feed scroll detected! Blocking...")
                    blockScroll()
                } else {
                    Log.d(TAG, "✅ Not news feed (Messenger/Friends/etc) - allowing scroll")
                }
            }
        }
    }

    /**
     * Проверяет, что на экране открыта именно лента новостей VK.
     * Мессенджер, Друзья и Комментарии не имеют этих признаков.
     */
    private fun isNewsFeed(rootNode: AccessibilityNodeInfo): Boolean {
        // Признак 1: Существует вкладка "Главная" в нижней навигации
        val homeNodes = rootNode.findAccessibilityNodeInfosByText("Главная")
        val hasHomeTab = homeNodes.any { node ->
            val desc = node.contentDescription?.toString() ?: ""
            // Проверяем, что это именно элемент навигации
            desc == "Главная" || desc.startsWith("Главная ") || node.isSelected
        }

        // Признак 2: Есть элементы, характерные только для ленты (поле создания поста)
        // В мессенджере и списке друзей текста "Что у вас нового?" или кнопки "Создать" в шапке нет
        val hasFeedContent = rootNode.findAccessibilityNodeInfosByText("Что у вас нового?").isNotEmpty() ||
                rootNode.findAccessibilityNodeInfosByText("Создать").isNotEmpty()

        Log.d(TAG, "Feed check: hasHomeTab=$hasHomeTab, hasFeedContent=$hasFeedContent")

        // Блокируем только если есть вкладка "Главная" И контент ленты
        return hasHomeTab && hasFeedContent
    }

    /**
     * Блокирует скролл - переключает на вкладку "Главная"
     */
    private fun blockScroll() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScrollBlockTime < SCROLL_BLOCK_COOLDOWN) {
            Log.d(TAG, "⏳ Cooldown active, skipping")
            return
        }
        lastScrollBlockTime = currentTime

        Log.d(TAG, "🚫 BLOCKING SCROLL! Switching to Home...")

        Toast.makeText(this, "⏰ Лента заблокирована. Отдохните!", Toast.LENGTH_SHORT).show()

        // Переключаемся на вкладку "Главная"
        rootInActiveWindow?.let { rootNode ->
            switchToHomeTab(rootNode)
        }
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

            // Ищем именно вкладку навигации
            if (desc == "Главная" || desc.startsWith("Главная ")) {
                Log.d(TAG, "✓ Found Home: desc='$desc'")

                // Сначала фокусируемся
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)

                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {}

                // Потом кликаем
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

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ ScrollAccessibilityService connected!")
    }
}