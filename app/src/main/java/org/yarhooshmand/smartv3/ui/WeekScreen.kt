package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.reminders.Reminder
import org.yarhooshmand.smartv3.reminders.ReminderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeekScreen(repo: ReminderRepository, onToggleDone: (Reminder)->Unit) {
    var all by remember { mutableStateOf(listOf<Reminder>()) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var q by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        all = repo.getAll().first()
    }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val base = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SATURDAY
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }
    val days = (0..6).map {
        val c = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, it) }
        sdf.format(c.time)
    }

    val filtered = all.filter { r ->
        val matchQ = q.isBlank() || r.title.contains(q, true) || (r.note?.contains(q, true) ?: false)
        val matchDay = selectedDate?.let { it == r.date } ?: true
        matchQ && matchDay
    }

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = q,
            onValueChange = { q = it },
            label = { Text("جستجو بین یادآورها") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach { d ->
                val sel = selectedDate == d
                Surface(
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = if (sel) 2.dp else 0.dp,
                    border = if (sel) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .clickable { selectedDate = if (sel) null else d }
                ) {
                    Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(d.substring(5), fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
        Divider()
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("موردی برای نمایش نیست") }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(filtered) { r ->
                    ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            r.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("تاریخ: " + (r.date ?: ""))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AssistChip(onClick = { onToggleDone(r) }, label = { Text(if (r.done) "واگرد" else "خوردم") })
                                    AssistChip(onClick = { /* TODO: details */ }, label = { Text("جزئیات") })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
