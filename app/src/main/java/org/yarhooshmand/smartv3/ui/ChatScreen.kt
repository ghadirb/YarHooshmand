package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.yarhooshmand.smartv3.net.ChatClient

@Composable
fun ChatScreen() {
    val ctx = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }
    var history by rememberSaveable { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto scroll to bottom when new message added
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("💬 گفتگو با هوش مصنوعی", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState
        ) {
            items(history) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = message, 
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("پیام خود را بنویسید...") },
                enabled = !isLoading,
                maxLines = 3
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val t = input.trim()
                    if (t.isNotEmpty() && !isLoading) {
                        isLoading = true
                        history = history + "👤 شما: $t"
                        input = ""
                        
                        scope.launch {
                            val response = ChatClient.chatLocal(ctx, t)
                            history = history + "🤖 دستیار: $response"
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && input.trim().isNotEmpty()
            ) { 
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("ارسال") 
                }
            }
        }
    }
}