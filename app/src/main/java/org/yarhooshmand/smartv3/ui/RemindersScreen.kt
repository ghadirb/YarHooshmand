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
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable

@Composable
fun RemindersScreen() {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val dao = remember { db.reminderDao() }
    var text by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") } // human readable yyyy-MM-dd HH:mm
    var time by remember { mutableStateOf(System.currentTimeMillis() + 60_000) }
    var list by remember { mutableStateOf(listOf<ReminderEntity>()) }
    val scope = rememberCoroutineScope()
    var filter by remember { mutableStateOf(0) } // 0 all,1 pending,2 done
    var showTable by remember { mutableStateOf(true) }
    val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun reload() = scope.launch(Dispatchers.IO) { list = dao.getAll() }

    LaunchedEffect(Unit) { reload() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("یادآورها", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("متن") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = timeInput, onValueChange = { timeInput = it }, label = { Text("زمان (YYYY-MM-DD HH:mm)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        // Date & Time pickers (native dialogs)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            val act = ctx as? ComponentActivity
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = java.util.Calendar.getInstance()
                    val dp = android.app.DatePickerDialog(ctx, { _, y, m, d ->
                        // set date into timeInput as yyyy-MM-dd keeping time if present
                        val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                        val hh = existing?.hours ?: 9
                        val mm = existing?.minutes ?: 0
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                        val cal = java.util.Calendar.getInstance().apply { set(y, m, d, hh, mm) }
                        timeInput = sdf.format(java.util.Date(cal.timeInMillis))
                    }, nowCal.get(java.util.Calendar.YEAR), nowCal.get(java.util.Calendar.MONTH), nowCal.get(java.util.Calendar.DAY_OF_MONTH))
                    dp.show()
                }
            }) { Text("انتخاب تاریخ") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                act?.runOnUiThread {
                    val nowCal = java.util.Calendar.getInstance()
                    val tp = android.app.TimePickerDialog(ctx, { _, h, min ->
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                        val existing = try { dateFmt.parse(timeInput) } catch (_: Exception) { null }
                        val y = existing?.let { java.util.Calendar.getInstance().apply { time = it }?.get(java.util.Calendar.YEAR) } ?: nowCal.get(java.util.Calendar.YEAR)
                        val mon = existing?.let { java.util.Calendar.getInstance().apply { time = it }?.get(java.util.Calendar.MONTH) } ?: nowCal.get(java.util.Calendar.MONTH)
                        val d = existing?.let { java.util.Calendar.getInstance().apply { time = it }?.get(java.util.Calendar.DAY_OF_MONTH) } ?: nowCal.get(java.util.Calendar.DAY_OF_MONTH)
                        val cal = java.util.Calendar.getInstance().apply { set(y, mon, d, h, min) }
                        timeInput = sdf.format(java.util.Date(cal.timeInMillis))
                    }, nowCal.get(java.util.Calendar.HOUR_OF_DAY), nowCal.get(java.util.Calendar.MINUTE), true)
                    tp.show()
                }
            }) { Text("انتخاب زمان") }
        }
    
        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = {
                // parse timeInput or use time variable
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
                // export to file (app cache)
                scope.launch(Dispatchers.IO) {
                    val arr = dao.getAll().map { mapOf("id" to it.id, "text" to it.text, "timeMillis" to it.timeMillis, "done" to it.done) }
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    f.writeText(com.google.gson.Gson().toJson(arr))
                    Toast.makeText(ctx, "Exported to ${f.absolutePath}", Toast.LENGTH_LONG).show()
                }
            }) { Text("Export") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                // import from cache file if exists
                scope.launch(Dispatchers.IO) {
                    val f = File(ctx.cacheDir, "reminders_export.json")
                    if (!f.exists()) { Toast.makeText(ctx, "No export file found", Toast.LENGTH_SHORT).show(); return@launch }
                    val arr = com.google.gson.Gson().fromJson(f.readText(), Array<Map::class.java>::class.java)
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

        // Voice input to add reminder
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("افزودن با صدا:") 
            Button(onClick = {
                // Use Activity result to start speech recognizer - use a simple broadcast via Activity cast
                val act = ctx as? ComponentActivity ?: return@Button
                act.runOnUiThread {
                    val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "پیام خود را بگویید (مثال: یادآور دارو فردا ساعت 9)")
                    }
                    val launcher = act.activityResultRegistry.register("voice_add", ActivityResultContracts.StartActivityForResult()) { result ->
                        try {
                            val data = result.data
                            val words = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            val spoken = words?.firstOrNull() ?: ""
                            // use TimeParser to extract time
                            val parsed = try { org.yarhooshmand.smartv3.util.TimeParser.parsePersian(spoken) } catch (e: Exception) { null }
                            act.lifecycleScope.launch(Dispatchers.IO) {
                                val id = dao.insert(ReminderEntity(text = spoken, timeMillis = parsed ?: System.currentTimeMillis()+60000))
                                scheduleReminder(ctx, id, parsed ?: System.currentTimeMillis()+60000, spoken)
                                act.runOnUiThread { Toast.makeText(ctx, "یادآور اضافه شد", Toast.LENGTH_SHORT).show() }
                                reload()
                            }
                        } catch (ex: Exception) {
                        }
                    }
                    // launch speech intent
                    launcher.launch(i)
                }
            }) { Text("ضبط و اضافه کردن یادآور") }
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
                                Text(item.text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.clickable {
                                    // tapping text pre-fills for edit (not implemented fully)
                                })
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
            // compact tile view
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
