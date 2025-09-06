package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import org.yarhooshmand.smartv3.data.ReminderDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import java.util.Calendar

@Composable
fun WeekScreen() {
    val ctx = LocalContext.current
    val dao = remember { ReminderDatabase.getInstance(ctx).reminderDao() }
    val scope = rememberCoroutineScope()

    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis
    val end = start + 7L * 24 * 60 * 60 * 1000

    val weeks by dao.getAll().map { list -> list.filter { it.timeMillis in start until end } }
        .collectAsState(initial = emptyList())

    Scaffold { pad ->
        LazyColumn(contentPadding = pad, modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(weeks, key = { it.id }) { r: ReminderEntity ->
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.text)
                        Row {
                            TextButton(onClick = { scope.launch { dao.update(r.copy(done = !r.done)) } }) { Text(if (r.done) "لغو انجام" else "علامت انجام") }
                            Spacer(Modifier.width(8.dp))
                            Text("هفته جاری")
                        }
                    }
                }
            }
        }
    }
}
