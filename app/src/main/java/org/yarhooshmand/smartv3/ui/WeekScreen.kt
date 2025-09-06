package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.yarhooshmand.smartv3.data.Reminder
import org.yarhooshmand.smartv3.data.ReminderDatabase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeekScreen(context: android.content.Context) {
    val dao = remember { ReminderDatabase.get(context).reminderDao() }
    var reminders by remember { mutableStateOf<List<Reminder>>(emptyList()()) } // placeholder

    // collect flow safely
    LaunchedEffect(Unit) {
        dao.getAll().collectLatest { list ->
            reminders = list
        }
    }

    val sdfDay = remember { SimpleDateFormat("EEE dd MMM", Locale.getDefault()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("یادآورها (هفته)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // فیلتر هفته جاری
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.SATURDAY
        val start = cal.apply { set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0) }.timeInMillis
        val end = start + 7L * 24 * 60 * 60 * 1000

        val weekItems = reminders.filter { it.date in start..end }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(weekItems, key = { it.id }) { r ->
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.title, style = MaterialTheme.typography.titleMedium)
                        if (!r.note.isNullOrBlank()) {
                            Text(r.note!!, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(sdfDay.format(Date(r.date)), style = MaterialTheme.typography.labelSmall)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            val label = if (r.completed) "انجام شده ✅" else "علامت انجام"
                            TextButton(onClick = {
                                // ساده: علامت انجام
                                androidx.lifecycle.lifecycleScopeOrNull()?.launchWhenCreated {
                                    dao.markCompleted(r.id, System.currentTimeMillis())
                                }
                            }) { Text(label) }
                        }
                    }
                }
            }
        }
    }
}

// helper to get lifecycleScope safely from composable ambient (using LocalContext)
@Composable
private fun androidx.lifecycle.lifecycleScopeOrNull(): androidx.lifecycle.LifecycleCoroutineScope? {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    return if (ctx is androidx.lifecycle.LifecycleOwner) ctx.lifecycle.coroutineScope else null
}
