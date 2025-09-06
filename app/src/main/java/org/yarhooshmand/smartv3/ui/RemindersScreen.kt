package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.distinctUntilChanged
import org.yarhooshmand.smartv3.data.ReminderDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity

@Composable
fun RemindersScreen() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val dao = remember { ReminderDatabase.getInstance(ctx).reminderDao() }
    val reminders by dao.getAll()
        .distinctUntilChanged()
        .collectAsState(initial = emptyList())

    Scaffold { pad ->
        LazyColumn(
            contentPadding = pad,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders, key = { it.id }) { r: ReminderEntity ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.text, style = MaterialTheme.typography.titleMedium)
                        Text("زمان: ${r.timeMillis}")
                        if (r.category.isNotBlank()) Text("دسته: ${r.category}")
                    }
                }
            }
        }
    }
}
