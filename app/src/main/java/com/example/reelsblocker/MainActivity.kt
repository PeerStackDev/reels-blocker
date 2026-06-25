package com.example.reelsblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.reelsblocker.presentation.App
import com.example.reelsblocker.ui.theme.ReelsBlockerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReelsBlockerTheme {
                App()
            }
        }
    }
}

