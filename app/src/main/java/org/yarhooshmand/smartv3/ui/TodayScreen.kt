package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.data.ReminderRepository
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(repository: ReminderRepository) {
    val reminders by repository.allReminders.collectAsState(initial = emptyList())
    val startOfDay = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val endOfDay = startOfDay + 24 * 60 * 60 * 1000
    val today = remember(reminders) { reminders.filter { it.timeMillis in startOfDay until endOfDay } }

    Scaffold(topBar = { TopAppBar(title = { Text("امروز") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(today) { r -> Text(text = r.text, modifier = Modifier.padding(16.dp)) }
        }
    }
}
