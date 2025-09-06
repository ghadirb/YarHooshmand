package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.keys.KeysManager

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    var apiKey by remember { mutableStateOf(KeysManager.getActiveKeySafe(ctx) ?: "") }
    var status by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("OpenAI / OpenRouter API Key") }
        )
        Button(onClick = {
            KeysManager.setActiveKey(ctx, apiKey)
            status = "Saved"
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Save API Key")
        }
        if (status.isNotEmpty()) {
            Text(status, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
