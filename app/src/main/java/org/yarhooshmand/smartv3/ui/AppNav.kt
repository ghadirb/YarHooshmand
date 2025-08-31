
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
    val items = listOf("chat","models","reminders","settings")

    Scaffold(
        bottomBar = {
            NavigationBar {
                val back = nav.currentBackStackEntryAsState().value?.destination?.route ?: "chat"
                items.forEach { route ->
                    val icon = when(route){
                        "chat"-> Icons.Default.Chat
                        "models"-> Icons.Default.Hub
                        "reminders"-> Icons.Default.ViewList
                        else -> Icons.Default.Settings
                    }
                    NavigationBarItem(
                        selected = back == route,
                        onClick = { nav.navigate(route) },
                        label = { Text(route) },
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
