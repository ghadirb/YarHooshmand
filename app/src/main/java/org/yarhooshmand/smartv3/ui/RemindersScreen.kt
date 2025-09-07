package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(viewModel: ReminderViewModel) {
    val reminders = viewModel.reminders.collectAsState().value
    Scaffold(
        topBar = { TopAppBar(title = { Text("یادآورها") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(items = reminders, key = { it.id }) { r ->
                ReminderRow(
                    title = r.title,
                    note = r.note ?: "",
                    done = r.done,
                    onToggle = { viewModel.toggle(r.id) },
                    onDelete = { viewModel.delete(r.id) }
                )
            }
        }
    }
}
