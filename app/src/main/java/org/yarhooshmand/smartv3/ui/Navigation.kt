package org.yarhooshmand.smartv3.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("reminders") { RemindersScreen(navController) }
        // اگر صفحات دیگری داری می‌توانی اضافه کنی:
        // composable("chat") { ChatScreen(navController) }
        // composable("settings") { SettingsScreen(navController) }
    }
}
