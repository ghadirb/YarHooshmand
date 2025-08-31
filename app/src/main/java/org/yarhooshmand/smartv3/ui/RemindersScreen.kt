package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.reminders.scheduleReminder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RemindersScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.get(ctx).reminderDao() }
    var text by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("1") }
    var list by remember { mutableStateOf(listOf<ReminderEntity>()) }
    var isAdding by remember { mutableStateOf(false) }

    fun reload() {
        scope.launch(Dispatchers.IO) { 
            list = dao.getAll()
        }
    }

    LaunchedEffect(Unit) { reload() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("⏰ یادآورهای هوشمند", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        
        // Add new reminder section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("افزودن یادآور جدید", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("متن یادآور") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isAdding
                )
                
                Spacer(Modifier.height(8.dp))
                
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { if (it.all { char -> char.isDigit() }) minutes = it },
                        label = { Text("دقیقه") },
                        modifier = Modifier.weight(1f),
                        enabled = !isAdding
                    )
                    
                    Spacer(Modifier.width(12.dp))
                    
                    Button(
                        onClick = {
                            val mins = minutes.toLongOrNull() ?: 1L
                            if (text.trim().isNotEmpty() && mins > 0) {
                                isAdding = true
                                val whenMs = System.currentTimeMillis() + mins * 60_000L
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val newId = dao.insert(ReminderEntity(text = text.trim(), timeMillis = whenMs))
                                        scheduleReminder(ctx, newId, whenMs, text.trim())
                                        launch(Dispatchers.Main) {
                                            text = ""
                                            minutes = "1"
                                            isAdding = false
                                        }
                                        reload()
                                    } catch (e: Exception) {
                                        launch(Dispatchers.Main) { isAdding = false }
                                    }
                                }
                            }
                        },
                        enabled = !isAdding && text.trim().isNotEmpty() && (minutes.toLongOrNull() ?: 0) > 0,
                        modifier = Modifier.height(56.dp)
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("افزودن")
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Reminders list
        if (list.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp