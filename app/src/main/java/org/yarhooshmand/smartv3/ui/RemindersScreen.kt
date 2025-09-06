package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.data.ReminderRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(repository: ReminderRepository, onReminderClick: (ReminderEntity) -> Unit = {}) {
    val reminders by repository.allReminders.collectAsState(initial = emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("یادآورها") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(reminders) { r ->
                ReminderItem(r) { onReminderClick(r) }
            }
        }
    }
}

@Composable
private fun ReminderItem(reminder: ReminderEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), onClick = onClick) {
        Column(Modifier.padding(16.dp)) {
            Text(text = reminder.text, style = MaterialTheme.typography.titleMedium)
            Text(text = "زمان: ${reminder.timeMillis}", style = MaterialTheme.typography.bodySmall)
            reminder.category?.let { Text(text = "دسته: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}
