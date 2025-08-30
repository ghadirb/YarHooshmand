
package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.reminders.scheduleReminder

@Composable
fun RemindersScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.get(ctx).reminderDao() }
    var text by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("1") }
    var list by remember { mutableStateOf(listOf<ReminderEntity>()) }

    fun reload() {
        scope.launch(Dispatchers.IO) { list = dao.getAll() }
    }

    LaunchedEffect(Unit) { reload() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            OutlinedTextField(text, { text = it }, label = { Text("متن یادآور") }, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(minutes, { minutes = it }, label = { Text("دقیقه") }, modifier = Modifier.width(90.dp))
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val mins = minutes.toLongOrNull() ?: 1L
                val whenMs = System.currentTimeMillis() + mins*60_000L
                scope.launch(Dispatchers.IO) {
                    val id = dao.insert(ReminderEntity(text = text.trim(), timeMillis = whenMs))
                    scheduleReminder(ctx, id, whenMs, text.trim())
                    reload()
                }
                text = ""; minutes = "1"
            }) { Text("افزودن") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(list) { item ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(item.text)
                            Text(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(item.timeMillis)),
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Checkbox(checked = item.done, onCheckedChange = { checked ->
                            scope.launch(Dispatchers.IO) {
                                dao.update(item.copy(done = checked))
                                reload()
                            }
                        })
                    }
                }
            }
        }
    }
}
