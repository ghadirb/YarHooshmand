package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.yarhooshmand.smartv3.reminders.ReminderRepository
import org.yarhooshmand.smartv3.reminders.reminderToggleDone

@Composable
fun AppNav(repo: ReminderRepository) {
    val navController = rememberNavController()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0; navController.navigate("today") { popUpTo("today") { inclusive = true } } },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("امروز") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1; navController.navigate("week") { popUpTo("today") { inclusive = false } } },
                    icon = { Icon(Icons.Default.CalendarToday, null) },
                    label = { Text("هفته") }
                )
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "today", modifier = Modifier.padding(padding)) {
            composable("today") { TodayScreen(repo) }
            composable("week") { WeekScreen(repo) { r -> reminderToggleDone(repo, r) } }
        }
    }
}
