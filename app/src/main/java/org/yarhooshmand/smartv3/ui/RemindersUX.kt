package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.yarhooshmand.smartv3.data.local.Reminder
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@Composable
fun RemindersScreen(viewModel: ReminderViewModel, navController: NavController) {
    val reminders = viewModel.allReminders.value ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("یادآورها") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add new reminder screen */ }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            items(reminders) { reminder ->
                ReminderItem(reminder = reminder)
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = reminder.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = reminder.description ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
