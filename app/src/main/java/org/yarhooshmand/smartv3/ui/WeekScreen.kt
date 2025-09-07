package org.yarhooshmand.smartv3.ui

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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekScreen(viewModel: ReminderViewModel) {
    val all = viewModel.reminders.collectAsState().value
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    val start = cal.timeInMillis
    val end = start + 7L*24*60*60*1000
    val week = all.filter { it.date != null && it.date in start until end }

    Scaffold(topBar = { TopAppBar(title = { Text("این هفته") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(week, key = { it.id }) { r ->
                ReminderRow(
                    title = r.title, note = r.note ?: "", done = r.done,
                    onToggle = { viewModel.toggle(r.id) },
                    onDelete = { viewModel.delete(r.id) }
                )
            }
        }
    }
}
