package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.data.ReminderRepository
import java.util.*

@Composable
fun TodayScreen(repository: ReminderRepository) {
    val reminders by repository.allReminders.collectAsState(initial = emptyList())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayReminders = reminders.filter { it.timeMillis >= today }

    Scaffold(
        topBar = { TopAppBar(title = { Text("امروز") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(todayReminders) { reminder ->
                Text(
                    text = reminder.text,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
