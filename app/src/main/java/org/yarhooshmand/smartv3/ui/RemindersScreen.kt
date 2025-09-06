package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel = viewModel()
) {
    val reminders by reminderViewModel.reminders.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("یادآورها") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add reminder */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            reminders.forEach { reminder ->
                ListItem(
                    headlineContent = { Text(reminder.title) },
                    supportingContent = {
                        Text(reminder.description ?: "")
                    }
                )
                Divider()
            }
        }
    }
}
