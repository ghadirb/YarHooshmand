package org.yarhooshmand.smartv3.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.net.ChatClient

@Composable
fun ActiveModelsScreen() {
    val ctx = LocalContext.current
    var result by remember { mutableStateOf("...") }
    LaunchedEffect(Unit) {
        result = ChatClient.testModel(ctx, "hello")
    }
    Text(result)
}
