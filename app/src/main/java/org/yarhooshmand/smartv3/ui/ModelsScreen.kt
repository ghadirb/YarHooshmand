
package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.models.ModelManager

@Composable
fun ModelsScreen() {
    val ctx = LocalContext.current
    var list by rememberSaveable { mutableStateOf(ModelManager.getModels(ctx).map { it.copy() }) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("مدل‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = {
                ModelManager.refreshStatuses(ctx)
                list = ModelManager.getModels(ctx).map { it.copy() }
            }) { Text("بروزرسانی وضعیت") }
            Spacer(Modifier.width(8.dp))
            Text("اولویت: 4o-mini → 4o → 3.5", modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(list) { m ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Checkbox(checked = m.enabled, onCheckedChange = {
                            ModelManager.updateModelEnabled(ctx, m.name, it)
                            list = ModelManager.getModels(ctx).map { it.copy() }
                        })
                        Spacer(Modifier.width(8.dp))
                        Text(if (m.online) "✓ آنلاین" else "○ آفلاین", Modifier.width(72.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(m.name)
                    }
                }
            }
        }
    }
}
