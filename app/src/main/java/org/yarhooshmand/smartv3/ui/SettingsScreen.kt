
package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.models.ModelManager

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    var simple by remember { mutableStateOf(ModelManager.isSimpleMode(ctx)) }
    var listen by remember { mutableStateOf(ModelManager.isListenAfterAlarm(ctx)) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("حالت ساده")
            Switch(simple, onCheckedChange = { simple = it; ModelManager.setSimpleMode(ctx, it) })
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("شنود کوتاه‌مدت بعد از یادآوری")
            Switch(listen, onCheckedChange = { listen = it; ModelManager.setListenAfterAlarm(ctx, it) })
        }
        Spacer(Modifier.height(12.dp))
        Text("کلیدها از Google Drive به‌صورت خودکار بارگذاری می‌شوند.")
    }
}
