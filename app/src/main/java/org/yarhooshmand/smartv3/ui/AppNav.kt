package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Hub

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route ?: "chat"
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple("chat", "گفتگو", Icons.Filled.Chat),
                    Triple("models", "مدل‌ها", Icons.Filled.Hub),
                    Triple("reminders", "یادآورها", Icons.Filled.ViewList),
                    Triple("settings", "تنظیمات", Icons.Filled.Settings),
                ).forEach { (r, label, icon) ->
                    NavigationBarItem(
                        selected = route == r,
                        onClick = { nav.navigate(r) },
                        label = { Text(label) },
                        icon = { Icon(icon, contentDescription = null) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, "chat", Modifier.padding(pad)) {
            composable("chat") { ChatScreen() }
            composable("models") { ModelsScreen() }
            composable("reminders") { RemindersScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
