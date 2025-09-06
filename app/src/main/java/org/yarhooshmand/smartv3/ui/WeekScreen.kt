package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@Composable
fun WeekScreen(viewModel: ReminderViewModel) {
    val reminders = viewModel.allReminders.value ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("یادآورهای این هفته") })
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
