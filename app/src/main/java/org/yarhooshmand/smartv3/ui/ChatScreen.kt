package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.net.ChatClient

@Composable
fun ChatScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Prompt") }
        )

        Button(onClick = {
            if (input.isBlank()) return@Button
            loading = true
            val prompt = input.trim()
            scope.launch {
                try {
                    val response = ChatClient.send(ctx, prompt)
                    messages = messages + ("You: $prompt\nAI: $response")
                } catch (t: Throwable) {
                    messages = messages + ("Error: ${t.message}")
                } finally {
                    loading = false
                }
            }
            input = ""
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text(if (loading) "Sending..." else "Send")
        }

        LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
            items(messages) { m ->
                Text(text = m, modifier = Modifier.padding(6.dp))
            }
        }
    }
}
