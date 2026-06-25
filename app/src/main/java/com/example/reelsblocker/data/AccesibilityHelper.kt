package com.example.reelsblocker.data

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

object AccessibilityHelper {

    private const val TAG = "AccessibilityHelper"

    /**
     * Проверяет, включен ли сервис специальных возможностей
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val serviceName = "${context.packageName}/${context.packageName}.data.MainAccessibilityService"
        Log.d(TAG, "Checking service: $serviceName")

        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            Log.d(TAG, "Enabled services string: $enabledServices")

            if (enabledServices.isNullOrEmpty()) {
                Log.d(TAG, "❌ No accessibility services enabled")
                return false
            }

            // Проверяем, есть ли наш сервис в списке включенных
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)

            var found = false
            while (colonSplitter.hasNext()) {
                val enabledService = colonSplitter.next()
                Log.d(TAG, "Found enabled service: $enabledService")

                if (enabledService.equals(serviceName, ignoreCase = true)) {
                    Log.d(TAG, "✅ Our service is enabled!")
                    found = true
                }
            }

            if (!found) {
                Log.d(TAG, " Our service NOT found in enabled services")
                Log.d(TAG, "Expected: $serviceName")
            }

            found
        } catch (e: Exception) {
            Log.e(TAG, "Error checking accessibility service", e)
            false
        }
    }

    /**
     * Открывает настройки специальных возможностей
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Log.d(TAG, "Opened accessibility settings")
        } catch (e: Exception) {
            Log.e(TAG, "Error opening accessibility settings", e)
        }
    }
}