package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.saveable.rememberSaveable
import org.yarhooshmand.smartv3.net.ChatClient

@Composable
fun ChatScreen() {
    var input by rememberSaveable { mutableStateOf("") }
    var history by rememberSaveable { mutableStateOf(listOf<String>()) }
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("گفتگو", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.weight(1f).verticalScroll(scroll)) {
            history.forEach { line ->
                Text(line, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
            }
        }
        Row {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("پیام...") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val t = input.trim()
                if (t.isNotEmpty()) {
                    history = history + "👤: $t"
                    history = history + ("🤖: " + (ChatClient.chatLocal(t) ?: "پاسخی دریافت نشد"))
                    input = ""
                }
            }) { Text("ارسال") }
        }
    }
}
