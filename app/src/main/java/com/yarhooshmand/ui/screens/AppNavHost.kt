package com.yarhooshmand.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.yarhooshmand.ui.ReminderViewModel

@Composable
fun AppNavHost(viewModel: ReminderViewModel) {
    val navController = rememberNavController()
    var selected by remember { mutableStateOf("main") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("یادآورها") },
                    selected = selected == "main", onClick = { selected = "main"; navController.navigate("main") })
                NavigationBarItem(icon = { Icon(Icons.Default.Add, null) }, label = { Text("افزودن") },
                    selected = selected == "add", onClick = { selected = "add"; navController.navigate("add") })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("تنظیمات") },
                    selected = selected == "settings", onClick = { selected = "settings"; navController.navigate("settings") })
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "main", modifier = Modifier.padding(padding)) {
            composable("main") { MainScreen(viewModel) }
            composable("add") { AddReminderScreen(viewModel) }
            composable("settings") { SettingsScreen() }
        }
    }
}
