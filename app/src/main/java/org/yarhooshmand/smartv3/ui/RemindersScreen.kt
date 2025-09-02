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
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.reminders.scheduleReminder
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun RemindersScreen() {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val dao = remember { db.reminderDao() }
    var text by rememberSaveable { mutableStateOf("") }
    var timeInput by rememberSaveable { mutableStateOf("") }
    var list by remember { mutableStateOf(listOf<ReminderEntity>()) }
    val scope = rememberCoroutineScope()
    var filter by remember { mutableStateOf(0) }
    var showTable by remember { mutableStateOf(true) }
    val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun reload() = scope.launch(Dispatchers.IO) {
        val all = dao.getAll()
        withContext(Dispatchers.Main) { list = all }
    }

    LaunchedEffect(Unit) { reload() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("یادآورها", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("متن") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = timeInput,
            onValueChange = { timeInput = it },
            label = { Text("زمان (YYYY-MM-DD HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Date & Time pickers
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            val act = ctx as? ComponentActivity
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = Calendar.getInstance()
                    val dp = android.app.DatePickerDialog(ctx, { _, y, m, d ->
                        val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                        val cal = Calendar.getInstance().apply {
                            time = existing ?: Date()
                            set(Calendar.YEAR, y)
                            set(Calendar.MONTH, m)
                            set(Calendar.DAY_OF_MONTH, d)
                        }
                        timeInput = dateFmt.format(cal.time)
                    }, nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DAY_OF_MONTH))
                    dp.show()
                }
            }) { Text("انتخاب تاریخ") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = Calendar.getInstance()
                    val tp = android.app.TimePickerDialog(ctx, { _, h, min ->
                        val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                        val cal = Calendar.getInstance().apply {
                            time = existing ?: Date()
                            set(Calendar.HOUR_OF_DAY, h)
                            set(Calendar.MINUTE, min)
                        }
                        timeInput = dateFmt.format(cal.time)
                    }, nowCal.get(Calendar.HOUR_OF_DAY), nowCal.get(Calendar.MINUTE), true)
                    tp.show()
                }
            }) { Text("انتخاب زمان") }
        }

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = {
                val parsed = try { dateFmt.parse(timeInput)?.time ?: System.currentTimeMillis() + 60_000 } catch (_: Exception) { System.currentTimeMillis() + 60_000 }
                scope.launch(Dispatchers.IO) {
                    val id = dao.insert(ReminderEntity(text = text.ifBlank { "یادآوری" }, timeMillis = parsed))
                    scheduleReminder(ctx, id, parsed, text)
                    withContext(Dispatchers.Main) {
                        text = ""
                        timeInput = ""
                    }
                    reload()
                }
            }) { Text("ثبت") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val arr = dao.getAll().map { mapOf("id" to it.id, "text" to it.text, "timeMillis" to it.timeMillis, "done" to it.done) }
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    f.writeText(com.google.gson.Gson().toJson(arr))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(ctx, "Exported to ${f.absolutePath}", Toast.LENGTH_LONG).show()
                    }
                }
            }) { Text("Export") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    if (!f.exists()) {
                        withContext(Dispatchers.Main) { Toast.makeText(ctx, "No export file found", Toast.LENGTH_SHORT).show() }
                        return@launch
                    }
                    val arr = com.google.gson.Gson().fromJson(f.readText(), Array<Map::class.java>::class.java)
                    for (m in arr) {
                        val t = (m["text"] as? String) ?: "یادآوری"
                        val tm = (m["timeMillis"] as? Double)?.toLong() ?: (m["timeMillis"] as? Long) ?: System.currentTimeMillis() + 60_000
                        dao.insert(ReminderEntity(text = t, timeMillis = tm))
                    }
                    reload()
                }
            }) { Text("Import") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = { showTable = !showTable }) { Text(if (showTable) "نمایش لیست" else "نمایش جدول") }
        }

        Spacer(Modifier.height(12.dp))

        // Filters
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { filter = 0 }) { Text("همه") }
            Button(onClick = { filter = 1 }) { Text("در انتظار") }
            Button(onClick = { filter = 2 }) { Text("انجام‌شده") }
        }

        Spacer(Modifier.height(8.dp))

        // List / Table view
        val display = when(filter) {
            1 -> list.filter { !it.done }
            2 -> list.filter { it.done }
            else -> list
        }

        if (showTable) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(display) { item ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item.text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.clickable { })
                                Text(dateFmt.format(Date(item.timeMillis)), style = MaterialTheme.typography.bodySmall)
                            }
                            Column {
                                Checkbox(checked = item.done, onCheckedChange = { checked ->
                                    scope.launch(Dispatchers.IO) {
                                        dao.update(item.copy(done = checked))
                                        reload()
                                    }
                                })
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        dao.delete(item)
                                        reload()
                                    }
                                }) { Text("حذف") }
                            }
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                display.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.text)
                        Text(dateFmt.format(Date(item.timeMillis)))
                    }
                }
            }
        }
    }
}
