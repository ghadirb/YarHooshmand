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
        startDestination = "reminders"
    ) {
        composable("reminders") {
            RemindersScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        composable("chat") {
            ChatScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}
