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
fun WeekScreen(repository: ReminderRepository) {
    val reminders by repository.allReminders.collectAsState(initial = emptyList())
    val cal = Calendar.getInstance()
    val startOfWeek = cal.apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeek) }.timeInMillis
    val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000

    val weekReminders = reminders.filter { it.timeMillis in startOfWeek..endOfWeek }

    Scaffold(
        topBar = { TopAppBar(title = { Text("این هفته") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(weekReminders) { reminder ->
                Text(
                    text = reminder.text,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
