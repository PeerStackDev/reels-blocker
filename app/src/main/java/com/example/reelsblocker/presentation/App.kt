package com.example.reelsblocker.presentation

import android.app.Application
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reelsblocker.data.AccessibilityHelper
import com.example.reelsblocker.ui.theme.BACKGROUND

@Preview
@Composable
fun App() {

    var showPermissionDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val application = context.applicationContext as Application

    // для Антирилс
    val antiReelsViewModel: AntiReelsViewModel = viewModel(
        factory = AntiReelsViewModelFactory(application)
    )
    // для антискролл
    val antiScrollViewModel: AntiScrollViewModel = viewModel(
        factory = AntiScrollViewModelFactory(application)
    )
    // для настроек(режимов)
    val viewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(application)
    )


    // Проверяем права при запуске
    LaunchedEffect(Unit) {
        if (!AccessibilityHelper.isAccessibilityServiceEnabled(context)) {
            showPermissionDialog = true
        }
    }

    val navController = rememberNavController()

    Scaffold(
        containerColor = BACKGROUND,
        bottomBar = {
            FooterNav(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(padding)
        ) {

            composable(
                route = "main",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        tween(300)
                    )
                }
            )
            {
                MainScreen(
                    antiScrollViewModel = antiScrollViewModel,
                    antiReelsViewModel = antiReelsViewModel,
                    onNavigateToAntiReels = {
                        navController.navigate("antireels")
                    },
                    onNavigateToAntiScroll = {
                        navController.navigate("antiscroll")
                    }
                )
            }

            composable(
                route = "settings",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        tween(300)
                    )
                }
            ) {
                SettingScreen(viewModel = viewModel)
            }

            composable(
                route = "info",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        tween(300)
                    )
                }
            ) {
               InfoScreen()
            }
            composable(
                route = "antireels",
            )
                {
                AntiReelsScreen(
                    viewModel = antiReelsViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "antiscroll",
            ) {
                AntiScrollScreen(
                    viewModel = antiScrollViewModel,
                    onBack = {

                        navController.popBackStack()
                    }
                )
            }
        }
    }
    // Диалог с просьбой выдать права
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onOpenSettings = {
                AccessibilityHelper.openAccessibilitySettings(context)
                showPermissionDialog = false
            }
        )
    }
}