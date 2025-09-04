package com.yarhooshmand.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yarhooshmand.backup.ExportImport
import com.yarhooshmand.data.Reminder
import com.yarhooshmand.ui.ReminderViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: ReminderViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reminders by viewModel.reminders
    var tableMode by remember { mutableStateOf(false) }

    val createJson = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        if (uri != null) scope.launch { ExportImport.exportJson(context, uri, reminders) }
    }
    val createCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        if (uri != null) scope.launch { ExportImport.exportCsv(context, uri, reminders) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("یادآور هوشمند") },
                actions = {
                    TextButton(onClick = { tableMode = !tableMode }) { Text(if (tableMode) "لیست" else "جدول") }
                    TextButton(onClick = { createJson.launch("reminders.json") }) { Text("Export JSON") }
                    TextButton(onClick = { createCsv.launch("reminders.csv") }) { Text("Export CSV") }
                }
            )
        }
    ) { padding ->
        if (!tableMode) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderCard(reminder, onToggle = { viewModel.toggleDone(reminder) },
                        onDelete = { viewModel.deleteReminder(reminder) })
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(6.dp)) {
                    Text("عنوان", Modifier.weight(0.5f))
                    Text("تاریخ", Modifier.weight(0.25f))
                    Text("ساعت", Modifier.weight(0.25f))
                }
                Divider()
                reminders.forEach { r ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(r.title, Modifier.weight(0.5f))
                        Text(r.date, Modifier.weight(0.25f))
                        Text(r.time, Modifier.weight(0.25f))
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isDone) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                Text("${reminder.date}  ${reminder.time}", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                TextButton(onClick = onToggle) { Text(if (reminder.isDone) "بازگردانی" else "انجام شد") }
                TextButton(onClick = onDelete) { Text("حذف") }
            }
        }
    }
}
