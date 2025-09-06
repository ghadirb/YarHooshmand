package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.models.ModelManager

@Composable
fun ModelsScreen() {
    val ctx = LocalContext.current
    val models = remember { mutableStateListOf(*ModelManager.all().toTypedArray()) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("مدل‌ها", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        models.forEachIndexed { idx, m ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(m.name)
                Switch(checked = m.enabled, onCheckedChange = {
                    m.enabled = it
                    models[idx] = m.copy()
                    ModelManager.setEnabled(ctx, m.name, it)
                })
            }
            Divider()
        }
    }
}
