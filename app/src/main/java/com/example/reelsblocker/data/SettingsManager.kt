package com.example.reelsblocker.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("reels_blocker_prefs", Context.MODE_PRIVATE)

    // Чтение
    fun isAntiReelsEnabled(): Boolean = prefs.getBoolean("anti_reels", false)
    fun isVkBlocked(): Boolean = prefs.getBoolean("vk_block", false)
    fun isYouTubeBlocked(): Boolean = prefs.getBoolean("youtube_block", false)
    fun isRuTubeBlocked(): Boolean = prefs.getBoolean("rutube_block", false)

    // Запись
    fun setAntiReelsEnabled(enabled: Boolean) = prefs.edit().putBoolean("anti_reels", enabled).apply()
    fun setVkBlocked(enabled: Boolean) = prefs.edit().putBoolean("vk_block", enabled).apply()
    fun setYouTubeBlocked(enabled: Boolean) = prefs.edit().putBoolean("youtube_block", enabled).apply()
    fun setRuTubeBlocked(enabled: Boolean) = prefs.edit().putBoolean("rutube_block", enabled).apply()

    // Список пакетов для блокировки (нужен для сервиса)
    fun getBlockedPackages(): List<String> {
        val list = mutableListOf<String>()
        if (isVkBlocked()) list.add("com.vkontakte.android")
        if (isYouTubeBlocked()) list.add("com.google.android.youtube")
        if (isRuTubeBlocked()) list.add("ru.rutube.app")
        return list
    }

    // === AntiScroll ===
    fun isAntiScrollEnabled(): Boolean = prefs.getBoolean("anti_scroll", false)
    fun isVkScrollBlocked(): Boolean = prefs.getBoolean("vk_scroll_block", false)
    fun isYouTubeScrollBlocked(): Boolean = prefs.getBoolean("youtube_scroll_block", false)

    fun setAntiScrollEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("anti_scroll", enabled).apply()
    }

    fun setVkScrollBlocked(enabled: Boolean) {
        prefs.edit().putBoolean("vk_scroll_block", enabled).apply()
    }

    fun setYouTubeScrollBlocked(enabled: Boolean) {
        prefs.edit().putBoolean("youtube_scroll_block", enabled).apply()
    }
}