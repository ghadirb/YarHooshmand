package org.yarhooshmand.smartv3.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.reminders.scheduleReminder
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RemindersScreen() {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val dao = remember { db.reminderDao() }
    var text by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var list by remember { mutableStateOf(listOf<ReminderEntity>()) }
    val scope = rememberCoroutineScope()
    val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    var showTable by remember { mutableStateOf(true) }

    fun reload() = scope.launch(Dispatchers.IO) { list = dao.getAll() }

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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            val act = ctx as? ComponentActivity
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = Calendar.getInstance()
                    val dp = android.app.DatePickerDialog(
                        ctx,
                        { _, y, m, d ->
                            val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                            val hh = existing?.hours ?: 9
                            val mm = existing?.minutes ?: 0
                            val cal = Calendar.getInstance().apply { set(y, m, d, hh, mm) }
                            timeInput = dateFmt.format(Date(cal.timeInMillis))
                        },
                        nowCal.get(Calendar.YEAR),
                        nowCal.get(Calendar.MONTH),
                        nowCal.get(Calendar.DAY_OF_MONTH)
                    )
                    dp.show()
                }
            }) { Text("انتخاب تاریخ") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = Calendar.getInstance()
                    val tp = android.app.TimePickerDialog(
                        ctx,
                        { _, h, min ->
                            val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                            val y = existing?.let { Calendar.getInstance().apply { time = it }?.get(Calendar.YEAR) }
                                ?: nowCal.get(Calendar.YEAR)
                            val mon = existing?.let { Calendar.getInstance().apply { time = it }?.get(Calendar.MONTH) }
                                ?: nowCal.get(Calendar.MONTH)
                            val d = existing?.let { Calendar.getInstance().apply { time = it }?.get(Calendar.DAY_OF_MONTH) }
                                ?: nowCal.get(Calendar.DAY_OF_MONTH)
                            val cal = Calendar.getInstance().apply { set(y!!, mon!!, d!!, h, min) }
                            timeInput = dateFmt.format(Date(cal.timeInMillis))
                        },
                        nowCal.get(Calendar.HOUR_OF_DAY),
                        nowCal.get(Calendar.MINUTE),
                        true
                    )
                    tp.show()
                }
            }) { Text("انتخاب زمان") }
        }

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = {
                val parsed = try { dateFmt.parse(timeInput)?.time ?: System.currentTimeMillis() + 60_000 } catch (e: Exception) { System.currentTimeMillis() + 60_000 }
                scope.launch(Dispatchers.IO) {
                    val id = dao.insert(ReminderEntity(text = text.ifBlank { "یادآوری" }, timeMillis = parsed))
                    scheduleReminder(ctx, id, parsed, text)
                    text = ""
                    timeInput = ""
                    reload()
                }
            }) { Text("ثبت") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val arr = dao.getAll().map { mapOf("id" to it.id, "text" to it.text, "timeMillis" to it.timeMillis, "done" to it.done) }
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    f.writeText(Gson().toJson(arr))
                    Toast.makeText(ctx, "Exported to ${f.absolutePath}", Toast.LENGTH_LONG).show()
                }
            }) { Text("Export") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    if (!f.exists()) { Toast.makeText(ctx, "No export file found", Toast.LENGTH_SHORT).show(); return@launch }
                    val type = object : com.google.gson.reflect.TypeToken<Array<Map<String, Any>>>() {}.type
                    val arr: Array<Map<String, Any>> = Gson().fromJson(f.readText(), type)
                    for (m in arr) {
                        val t = (m["text"] as? String) ?: "یادآوری"
                        val tm = (m["timeMillis"] as? Double)?.toLong() ?: (m["timeMillis"] as? Long) ?: System.currentTimeMillis()+60000
                        dao.insert(ReminderEntity(text = t, timeMillis = tm))
                    }
                    reload()
                }
            }) { Text("Import") }

            Spacer(Modifier.width(8.dp))
            Button(onClick = { showTable = !showTable }) { Text(if (showTable) "نمایش لیست" else "نمایش جدول") }
        }

        Spacer(Modifier.height(12.dp))

        if (showTable) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(list) { item ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.text, style = MaterialTheme.typography.titleMedium)
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
        }
    }
}
