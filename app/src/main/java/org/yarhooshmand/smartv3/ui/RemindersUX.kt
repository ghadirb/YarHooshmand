package org.yarhooshmand.smartv3.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.collectAsState
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
    val df = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    val scope = rememberCoroutineScope()

    // Flow<List<ReminderEntity>> -> State<List<ReminderEntity>>
    val items by dao.getAll()
        .distinctUntilChanged()
        .collectAsState(initial = emptyList())

    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
        ) {
            // --- SMS Preferences Card ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "ارسال پیامک برای یادآورها",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        var master by remember { mutableStateOf(SmsPrefs.isMasterEnabled(ctx)) }
                        Switch(
                            checked = master,
                            onCheckedChange = {
                                master = it
                                SmsPrefs.setMasterEnabled(ctx, it)
                                Toast.makeText(
                                    ctx,
                                    if (it) "SMS فعال شد" else "SMS غیرفعال شد",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
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
                        TextButton(onClick = { SmsPrefs.setDefaultNumber(ctx, number.trim()) }) {
                            Text("ذخیره شماره")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (items.isEmpty()) {
                Text("هیچ یادآوری ثبت نشده است.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items, key = { it.id }) { r ->
                        val smsEnabled = remember {
                            mutableStateOf(SmsPrefs.isEnabledForReminder(ctx, r.id))
                        }
                        ReminderCard(
                            entity = r,
                            smsEnabled = smsEnabled.value,
                            onToggleSms = {
                                val newVal = !smsEnabled.value
                                smsEnabled.value = newVal
                                SmsPrefs.setEnabledForReminder(ctx, r.id, newVal)
                                if (newVal && !SmsPrefs.isMasterEnabled(ctx)) {
                                    Toast.makeText(ctx, "SMS کلی غیرفعال است", Toast.LENGTH_SHORT).show()
                                }
                            },
                            dateText = df.format(Date(r.timeMillis)),
                            onToggleDone = {
                                scope.launch {
                                    dao.update(r.copy(done = !r.done))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddReminderDialog(
            onDismiss = { showAdd = false },
            onAdd = { text, whenMillis, category, smsTargets ->
                scope.launch {
                    val id = dao.insert(
                        ReminderEntity(
                            text = text,
                            timeMillis = whenMillis,
                            category = category,
                            smsTargets = smsTargets,
                            done = false
                        )
                    )
                    // زمان‌بندی آلارم برای آیتم جدید
                    scheduleReminder(ctx, id, whenMillis, text, smsTargets)
                    showAdd = false
                }
            }
        )
    }
}

@Composable
private fun ReminderCard(
    entity: ReminderEntity,
    smsEnabled: Boolean,
    onToggleSms: () -> Unit,
    dateText: String,
    onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    entity.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(onClick = {}, label = { Text(dateText) })
                    Spacer(Modifier.width(8.dp))
                    if (entity.done) AssistChip(
                        onClick = {},
                        label = { Text("انجام شد") },
                        leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Checkbox(checked = entity.done, onCheckedChange = { onToggleDone() })
            Spacer(Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onToggleSms() }
            ) {
                Icon(
                    Icons.Default.Sms,
                    contentDescription = null,
                    tint = if (smsEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.width(4.dp))
                Text(if (smsEnabled) "SMS" else "—")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Long, String, String?) -> Unit
) {
    val ctx = LocalContext.current
    var text by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Date(System.currentTimeMillis() + 30 * 60 * 1000)) } // +30m
    val df = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    var category by remember { mutableStateOf("عمومی") }
    var smsTargets by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("یادآور جدید") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("متن یادآور") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("زمان: ${df.format(date)}")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("دسته‌بندی") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = smsTargets,
                    onValueChange = { smsTargets = it },
                    label = { Text("شماره(ها)ی SMS با کاما جدا") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (text.isBlank()) {
                    Toast.makeText(ctx, "متن را وارد کنید", Toast.LENGTH_SHORT).show()
                } else {
                    onAdd(
                        text.trim(),
                        date.time,
                        category,
                        if (smsTargets.trim().isEmpty()) null else smsTargets.trim()
                    )
                }
            }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
