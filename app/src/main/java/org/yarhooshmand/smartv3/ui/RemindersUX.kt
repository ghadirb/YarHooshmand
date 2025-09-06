
package org.yarhooshmand.smartv3.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.reminders.scheduleReminder
import org.yarhooshmand.smartv3.utils.SmsPrefs
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersUX() {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).reminderDao() }
    var items by remember { mutableStateOf(listOf<ReminderEntity>()) }
    val scope = rememberCoroutineScope()
    val df = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        scope.launch { items = dao.getAll() }
    }

    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Edit, contentDescription = null) }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("ارسال پیامک برای یادآورها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        var master by remember { mutableStateOf(SmsPrefs.isMasterEnabled(ctx)) }
                        Switch(checked = master, onCheckedChange = {
                            master = it
                            SmsPrefs.setMasterEnabled(ctx, it)
                            Toast.makeText(ctx, if (it) "SMS فعال شد" else "SMS غیرفعال شد", Toast.LENGTH_SHORT).show()
                        })
                    }
                    Spacer(Modifier.height(6.dp))
                    var number by remember { mutableStateOf(SmsPrefs.getDefaultNumber(ctx)) }
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text("شماره پیش‌فرض (اختیاری)") },
                        trailingIcon = { Icon(Icons.Default.Sms, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { SmsPrefs.setDefaultNumber(ctx, number.trim()) }) { Text("ذخیره شماره") }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (items.isEmpty()) {
                Text("هیچ یادآوری ثبت نشده است.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items, key = { it.id }) { r ->
                        val smsEnabled = remember { mutableStateOf(SmsPrefs.isEnabledForReminder(ctx, r.id)) }
                        ReminderCard(
                            entity = r,
                            smsEnabled = smsEnabled.value,
                            onToggleDone = {
                                val updated = r.copy(done = !r.done)
                                scope.launch {
                                    dao.update(updated)
                                    items = dao.getAll()
                                }
                            },
                            onToggleSms = {
                                val newVal = !smsEnabled.value
                                smsEnabled.value = newVal
                                SmsPrefs.setEnabledForReminder(ctx, r.id, newVal)
                                if (newVal && !SmsPrefs.isMasterEnabled(ctx)) {
                                    Toast.makeText(ctx, "SMS کلی غیرفعال است", Toast.LENGTH_SHORT).show()
                                }
                            },
                            dateText = df.format(Date(r.timeMillis))
                        )
                    }
                }
            }
        }
    }

    if (showAdd) AddReminderDialog(
        onDismiss = { showAdd = false },
        onAdd = { text, whenMillis, category, smsTargets ->
            scope.launch {
                val id = dao.insert(ReminderEntity(text = text, timeMillis = whenMillis, category = category, smsTargets = smsTargets))
                scheduleReminder(ctx, id, whenMillis, text)
                items = dao.getAll()
                showAdd = false
            }
        }
    )
}

@Composable
private fun ReminderCard(
    entity: ReminderEntity,
    smsEnabled: Boolean,
    dateText: String,
    onToggleDone: ()->Unit,
    onToggleSms: ()->Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Alarm, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entity.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(onClick = {}, label = { Text(dateText) })
                    Spacer(Modifier.width(8.dp))
                    if (entity.done) AssistChip(onClick = {}, label = { Text("انجام شد") }, leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) })
                }
            }
            Spacer(Modifier.width(8.dp))
            Checkbox(checked = entity.done, onCheckedChange = { onToggleDone() })
            Spacer(Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onToggleSms() }) {
                Icon(Icons.Default.Sms, contentDescription = null, tint = if (smsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(4.dp))
                Text(if (smsEnabled) "SMS" else "—")
            }
        }
    }
}


@Composable
private fun AddReminderDialog(onDismiss: ()->Unit, onAdd: (String, Long, String, String?)->Unit) {
    val ctx = LocalContext.current
    var text by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Date(System.currentTimeMillis() + 30*60*1000)) } // +30m default
    val df = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    var category by remember { mutableStateOf("عمومی") }
    var smsTargets by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("یادآور جدید") },
        text = {
            Column {
                OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("متن یادآور") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("زمان: ${df.format(date)}")
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        date = Date(System.currentTimeMillis() + 60*60*1000) // +1h
                    }) { Text("+1 ساعت") }
                    Button(onClick = {
                        date = Date(System.currentTimeMillis() + 24*60*60*1000) // +1d
                    }) { Text("+1 روز") }
                }
                Spacer(Modifier.height(8.dp))
                // Category
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("دسته: ")
                    Spacer(Modifier.width(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Button(onClick = { expanded = true }) { Text(category) }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("عمومی") }, onClick = { category = "عمومی"; expanded = false })
                            DropdownMenuItem(text = { Text("دارو") }, onClick = { category = "دارو"; expanded = false })
                            DropdownMenuItem(text = { Text("کار") }, onClick = { category = "کار"; expanded = false })
                            DropdownMenuItem(text = { Text("ورزش") }, onClick = { category = "ورزش"; expanded = false })
                            DropdownMenuItem(text = { Text("شخصی") }, onClick = { category = "شخصی"; expanded = false })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = smsTargets, onValueChange = { smsTargets = it }, label = { Text("شماره‌های مقصد (با , جدا کنید، خالی برای شماره پیش‌فرض)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("برای زمان دقیق‌تر از صفحه اصلی یادآورها استفاده کنید.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (text.isBlank()) {
                    Toast.makeText(ctx, "متن را وارد کنید", Toast.LENGTH_SHORT).show()
                } else {
                    onAdd(text.trim(), date.time, category, if (smsTargets.trim().isEmpty()) null else smsTargets.trim())
                }
            }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
