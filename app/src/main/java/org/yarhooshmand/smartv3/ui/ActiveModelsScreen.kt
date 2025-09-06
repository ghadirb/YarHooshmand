package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.ai.*
import org.yarhooshmand.smartv3.utils.AISettingsPref

data class ActiveModel(val provider: ProviderType, val id: String, val display: String)

@Composable
fun ActiveModelsScreen(modifier: Modifier = Modifier) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var active by remember { mutableStateOf(loadActive(ctx)) }
    var defaultProv by remember { mutableStateOf(AISettingsPref.getDefaultProvider(ctx)) }
    var defaultModel by remember { mutableStateOf(AISettingsPref.getModel(ctx, defaultProv) ?: ModelAllowlist.defaultsByProvider[defaultProv]!!.id) }
    var testing by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = !testing, onClick = {
                testing = true
                results = emptyMap()
                scope.launch(Dispatchers.IO) {
                    val svc = AIService(ctx)
                    val res = mutableMapOf<String, String>()
                    active.forEach { m ->
                        val r = try { svc.testModel(m.provider, m.id).getOrThrow() } catch (t: Throwable) { "خطا: ${t.message}" }
                        res[m.display] = r.take(200)
                    }
                    results = res
                    testing = false
                }
            }) { Text(if (testing) "در حال تست..." else "تست همه مدل‌های فعال") }
            OutlinedButton(onClick = { active = loadActive(ctx) }) { Text("Refresh") }
        }

        Text("مدل‌های فعال:", style = MaterialTheme.typography.titleMedium)
        if (active.isEmpty()) {
            Text("هیچ مدلی فعال نیست. لطفاً کلید سرویس‌ها را در تنظیمات وارد کنید.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(active) { m ->
                    val isDefault = (m.provider == defaultProv) && (m.id == defaultModel)
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().clickable {
                            // set as default
                            AISettingsPref.setDefaultProvider(ctx, m.provider)
                            AISettingsPref.setModel(ctx, m.provider, m.id)
                            defaultProv = m.provider
                            defaultModel = m.id
                        }
                    ) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("${m.display} (${m.provider.name})", modifier = Modifier.weight(1f))
                            if (isDefault) AssistChip(onClick = {}, label = { Text("پیش‌فرض") })
                        }
                    }
                }
            }
        }

        if (results.isNotEmpty()) {
            Divider()
            Text("نتایج تست:", style = MaterialTheme.typography.titleSmall)
            results.forEach { (name, out) ->
                Text("• $name → ${out}")
            }
        }
    }
}

private fun loadActive(ctx: android.content.Context): List<ActiveModel> {
    val out = mutableListOf<ActiveModel>()
    ProviderType.values().forEach { p ->
        val key = AISettingsPref.getApiKey(ctx, p)
        if (!key.isNullOrEmpty()) {
            val models = when (p) {
                ProviderType.OPENAI -> ModelAllowlist.openAi
                ProviderType.OPENROUTER -> ModelAllowlist.openRouter
                ProviderType.ANTHROPIC -> ModelAllowlist.anthropic
            }
            val selected = AISettingsPref.getModel(ctx, p) ?: models.first().id
            models.forEach { md ->
                // فقط مدل انتخاب‌شده هر Provider را به عنوان «فعال» در نظر بگیریم:
                if (md.id == selected) out.add(ActiveModel(p, md.id, md.display))
            }
        }
    }
    return out
}
