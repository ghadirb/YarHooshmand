package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.models.ModelManager
import org.yarhooshmand.smartv3.keys.KeysManager
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    var keysTxt by remember { mutableStateOf("") }
    var simple by remember { mutableStateOf(ModelManager.isSimpleMode(ctx)) }
    var listen by remember { mutableStateOf(ModelManager.isListenAfterAlarm(ctx)) }
    var testing by remember { mutableStateOf(false) }
    var keyStatuses by remember { mutableStateOf(listOf<Pair<String,Boolean>>()) }
    val scope = rememberCoroutineScope()
    var activeModel by remember { mutableStateOf(ModelManager.firstEnabled() ?: "(هیچ)") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = keysTxt,
            onValueChange = { keysTxt = it },
            label = { Text("کلیدهای API (هر خط یک کلید)") },
            modifier = Modifier.fillMaxWidth().height(160.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = {
                val list = keysTxt.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
                KeysManager.setKeys(ctx, list)
                Toast.makeText(ctx, "کلیدها ذخیره شد", Toast.LENGTH_SHORT).show()
            }) { Text("ذخیره کلیدها") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                // Refresh from Drive and re-init
                scope.launch(Dispatchers.IO) {
                    KeysManager.init(ctx)
                    // run validation
                    val sp = mutableListOf<Pair<String,Boolean>>()
                    val saved = ctx.getSharedPreferences("yar_keys_prefs",0).getString("api_keys","") ?: ""
                    val list = if (saved.isBlank()) listOf<String>() else saved.split('|')
                    for (k in list) {
                        val ok = KeysManager.validateKey(k)
                        sp.add(Pair(k, ok))
                    }
                    keyStatuses = sp
                    activeModel = ModelManager.firstEnabled() ?: "(هیچ)"
                }
            }) { Text("Refresh from Drive") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                // validate keys
                testing = true
                scope.launch(Dispatchers.IO) {
                    val sp = mutableListOf<Pair<String,Boolean>>()
                    val saved = ctx.getSharedPreferences("yar_keys_prefs",0).getString("api_keys","") ?: ""
                    val list = if (saved.isBlank()) listOf<String>() else saved.split('|')
                    for (k in list) {
                        val ok = KeysManager.validateKey(k)
                        sp.add(Pair(k, ok))
                    }
                    keyStatuses = sp
                    testing = false
                }
            }) { Text(if (testing) "در حال تست..." else "Test Keys") }
        }

        Spacer(Modifier.height(12.dp))
        Text("مدل فعال: $activeModel", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text("وضعیت کلیدها:") 
        LazyColumn(Modifier.fillMaxWidth().height(160.dp)) {
            items(keyStatuses) { kv ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(kv.first, maxLines = 1)
                    Text(if (kv.second) "OK" else "BAD")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("حالت ساده")
            Switch(checked = simple, onCheckedChange = { v -> simple = v; ModelManager.setSimpleMode(ctx, v) })
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("شنود کوتاه‌مدت بعد از یادآوری")
            Switch(checked = listen, onCheckedChange = { v -> listen = v; ModelManager.setListenAfterAlarm(ctx, v) })
        }
    }
    Spacer(Modifier.height(12.dp))
    // Theme toggle
    val themePref = org.yarhooshmand.smartv3.utils.ThemePref
    var darkMode by remember { mutableStateOf(themePref.isDark(ctx)) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("حالت تاریک / روشن")
        Switch(checked = darkMode, onCheckedChange = { v ->
            darkMode = v
            themePref.setDark(ctx, v)
        })
    }
    Spacer(Modifier.height(12.dp))
    // Backup export/import
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val ok = org.yarhooshmand.smartv3.utils.BackupUtils.importRemindersFromUri(ctx, uri)
                launch(Dispatchers.Main) {
                    Toast.makeText(ctx, if (ok) "بک‌آپ وارد شد" else "خطا در وارد کردن بک‌آپ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    Row {
        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                val path = org.yarhooshmand.smartv3.utils.BackupUtils.exportRemindersToFile(ctx)
                launch(Dispatchers.Main) {
                    if (path != null) Toast.makeText(ctx, "بک‌آپ در: " + path, Toast.LENGTH_LONG).show()
                    else Toast.makeText(ctx, "خطا در گرفتن بک‌آپ", Toast.LENGTH_LONG).show()
                }
            }
        }) { Text("Export reminders") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = {
            // pick file
            launcher.launch("application/json")
        }) { Text("Import reminders") }
    }
    Spacer(Modifier.height(12.dp))

}

    
    // --- SMS section ---
    Divider()
    Text("پیامک یادآورها", style = MaterialTheme.typography.titleMedium)
    val ctxLocal = LocalContext.current
    var smsMaster by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.SmsPrefs.isMasterEnabled(ctxLocal)) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("فعال‌سازی ارسال پیامک (اختیاری)")
        Switch(checked = smsMaster, onCheckedChange = {
            smsMaster = it
            org.yarhooshmand.smartv3.utils.SmsPrefs.setMasterEnabled(ctxLocal, it)
        })
    }
    var smsNumber by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.SmsPrefs.getDefaultNumber(ctxLocal)) }
    OutlinedTextField(value = smsNumber, onValueChange = { smsNumber = it }, label = { Text("شماره پیش‌فرض") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Button(onClick = { org.yarhooshmand.smartv3.utils.SmsPrefs.setDefaultNumber(ctxLocal, smsNumber.trim()) }) {
            Text("ذخیره شماره")
        }
    }
    

    Spacer(Modifier.height(12.dp))
    Button(onClick = {
        // export reminders by reading DB and writing CSV via ExportCsv helper - run in coroutine
        val ctxLocal = LocalContext.current
        kotlinx.coroutines.GlobalScope.launch {
            try {
                val dao = org.yarhooshmand.smartv3.data.AppDatabase.get(ctxLocal).reminderDao()
                val all = dao.getAll()
                val gson = com.google.gson.Gson()
                val json = gson.toJson(all)
                val path = org.yarhooshmand.smartv3.utils.ExportCsv.exportReminders(ctxLocal, json)
                android.widget.Toast.makeText(ctxLocal, "Exported to: $path", android.widget.Toast.LENGTH_LONG).show()
            } catch (_: Exception) {}
        }
    }) { Text("Export reminders to CSV") }
    

    Divider()
    Text("مدیریت مدل‌های هوش مصنوعی", style = MaterialTheme.typography.titleMedium)

    var providerIndex by remember { mutableStateOf(if (org.yarhooshmand.smartv3.utils.AISettingsPref.getDefaultProvider(ctx) == org.yarhooshmand.smartv3.ai.ProviderType.OPENAI) 0 else 1) }
    val providers = listOf("OpenAI", "OpenRouter")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Provider:", modifier = Modifier.padding(end = 8.dp))
        ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
            OutlinedTextField(
                value = providers[providerIndex],
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.width(200.dp),
                label = { Text("ارائه‌دهنده") }
            )
        }
        Spacer(Modifier.width(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = providerIndex==0, onClick = {
                providerIndex = 0
                org.yarhooshmand.smartv3.utils.AISettingsPref.setDefaultProvider(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENAI)
            }, label = { Text("OpenAI") })
            FilterChip(selected = providerIndex==1, onClick = {
                providerIndex = 1
                org.yarhooshmand.smartv3.utils.AISettingsPref.setDefaultProvider(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER)
            }, label = { Text("OpenRouter") })
        }
    }

    Spacer(Modifier.height(8.dp))

    var openaiKey by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENAI) ?: "") }
    var openrouterKey by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER) ?: "") }

    OutlinedTextField(value = openaiKey, onValueChange = { openaiKey = it },
        label = { Text("OpenAI API Key") }, modifier = Modifier.fillMaxWidth())
    Button(onClick = {
        org.yarhooshmand.smartv3.utils.AISettingsPref.setApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENAI, openaiKey)
    }) { Text("ذخیره کلید OpenAI") }

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(value = openrouterKey, onValueChange = { openrouterKey = it },
        label = { Text("OpenRouter API Key") }, modifier = Modifier.fillMaxWidth())
    Button(onClick = {
        org.yarhooshmand.smartv3.utils.AISettingsPref.setApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER, openrouterKey)
    }) { Text("ذخیره کلید OpenRouter") }

    Spacer(Modifier.height(12.dp))
    Text("مدل‌های پیشنهادی (اولویت با فارسی):")

    val openAiModels = org.yarhooshmand.smartv3.ai.ModelAllowlist.openAi
    val openRouterModels = org.yarhooshmand.smartv3.ai.ModelAllowlist.openRouter

    var selectedOpenAi by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENAI) ?: openAiModels.first().id) }
    var selectedOpenRouter by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER) ?: openRouterModels.first().id) }

    Text("OpenAI:")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        openAiModels.forEach { md ->
            FilterChip(selected = selectedOpenAi == md.id, onClick = {
                selectedOpenAi = md.id
                org.yarhooshmand.smartv3.utils.AISettingsPref.setModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENAI, md.id)
            }, label = { Text(md.display) })
        }
    }

    Spacer(Modifier.height(8.dp))
    Text("OpenRouter:")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        openRouterModels.forEach { md ->
            FilterChip(selected = selectedOpenRouter == md.id, onClick = {
                selectedOpenRouter = md.id
                org.yarhooshmand.smartv3.utils.AISettingsPref.setModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER, md.id)
            }, label = { Text(md.display) })
        }
    }

    Spacer(Modifier.height(8.dp))
    var fb by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.isFallbackEnabled(ctx)) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Switch(checked = fb, onCheckedChange = {
            fb = it
            org.yarhooshmand.smartv3.utils.AISettingsPref.setFallbackEnabled(ctx, it)
        })
        Text("فعال‌سازی سوییچ خودکار در صورت فیلتر/خطا")
    }
    

    Divider()
    Text("سرویس Anthropic", style = MaterialTheme.typography.titleMedium)
    var anthropicKey by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.ANTHROPIC) ?: "") }
    OutlinedTextField(value = anthropicKey, onValueChange = { anthropicKey = it },
        label = { Text("Anthropic API Key") }, modifier = Modifier.fillMaxWidth())
    Button(onClick = {
        org.yarhooshmand.smartv3.utils.AISettingsPref.setApiKey(ctx, org.yarhooshmand.smartv3.ai.ProviderType.ANTHROPIC, anthropicKey)
    }) { Text("ذخیره کلید Anthropic") }

    Spacer(Modifier.height(8.dp))
    Text("مدل‌های Anthropic:")
    val anthropicModels = org.yarhooshmand.smartv3.ai.ModelAllowlist.anthropic
    var selectedAnthropic by remember { mutableStateOf(org.yarhooshmand.smartv3.utils.AISettingsPref.getModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.ANTHROPIC) ?: anthropicModels.first().id) }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        anthropicModels.forEach { md ->
            FilterChip(selected = selectedAnthropic == md.id, onClick = {
                selectedAnthropic = md.id
                org.yarhooshmand.smartv3.utils.AISettingsPref.setModel(ctx, org.yarhooshmand.smartv3.ai.ProviderType.ANTHROPIC, md.id)
            }, label = { Text(md.display) })
        }
    }

    Spacer(Modifier.height(16.dp))
    var testDialog by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    Button(onClick = {
        testDialog = true
        // run test in background
        androidx.lifecycle.viewmodel.compose.viewModel<androidx.lifecycle.ViewModel>()
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val svc = org.yarhooshmand.smartv3.ai.AIService(ctx)
            val r = svc.chat("سلام")
            if (r.isSuccess) testResult = r.getOrNull() else testResult = "خطا: " + (r.exceptionOrNull()?.message ?: "")
        }
    }) { Text("تست مدل انتخاب‌شده") }
    if (testDialog) {
        AlertDialog(
            onDismissRequest = { testDialog = false },
            confirmButton = { TextButton({ testDialog = false }) { Text("بستن") } },
            title = { Text("نتیجه تست مدل") },
            text = { Text(testResult ?: "در حال تست...") }
        )
    }
    

/* ===== Appearance & Active Models UI ===== */
@Composable
fun AppearanceSection() {
    Column {
        Text("انتخاب تم", style = MaterialTheme.typography.titleMedium)
        var isDark by remember { mutableStateOf(false) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = !isDark, onClick = { isDark = false }, label = { Text("روشن") })
            FilterChip(selected = isDark, onClick = { isDark = true }, label = { Text("تاریک") })
        }
        Text(
            "تغییر تم در لحظه روی صفحه‌ها اعمال می‌شود.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ActiveModelsSection(ctx: android.content.Context) {
    Text("مدل‌های فعال", style = MaterialTheme.typography.titleMedium)
    val providers = listOf(
        org.yarhooshmand.smartv3.ai.ProviderType.OPENAI to org.yarhooshmand.smartv3.ai.ModelAllowlist.openAi,
        org.yarhooshmand.smartv3.ai.ProviderType.OPENROUTER to org.yarhooshmand.smartv3.ai.ModelAllowlist.openRouter,
        org.yarhooshmand.smartv3.ai.ProviderType.ANTHROPIC to org.yarhooshmand.smartv3.ai.ModelAllowlist.anthropic
    )

    val active = mutableListOf<Pair<org.yarhooshmand.smartv3.ai.ProviderType, org.yarhooshmand.smartv3.ai.ModelDescriptor>>()
    providers.forEach { (p, list) ->
        val key = org.yarhooshmand.smartv3.utils.AISettingsPref.getApiKey(ctx, p)
        if (!key.isNullOrBlank()) {
            val sel = org.yarhooshmand.smartv3.utils.AISettingsPref.getModel(ctx, p) ?: list.first().id
            val md = list.firstOrNull { it.id == sel } ?: list.first()
            active += p to md
        }
    }

    var defaultProv = org.yarhooshmand.smartv3.utils.AISettingsPref.getDefaultProvider(ctx)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        active.forEach { (p, md) ->
            ElevatedCard {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(md.display, style = MaterialTheme.typography.titleSmall)
                        Text(p.name, style = MaterialTheme.typography.bodySmall)
                    }
                    val selected = defaultProv == p
                    AssistChip(
                        onClick = { 
                            defaultProv = p
                            org.yarhooshmand.smartv3.utils.AISettingsPref.setDefaultProvider(ctx, p)
                        },
                        label = { Text(if (selected) "فعال ✅" else "انتخاب") }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            var testResults by remember { mutableStateOf("—") }
            Button(onClick = {
                // test all active models
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    val svc = org.yarhooshmand.smartv3.ai.AIService(ctx)
                    val sb = StringBuilder()
                    active.forEach { (p, _) ->
                        val r = svc.chat("سلام")
                        sb.append(p.name).append(": ")
                        if (r.isSuccess) sb.append("موفق ✅\n") else sb.append("خطا ❌ ").append(r.exceptionOrNull()?.message).append("\n")
                    }
                    testResults = sb.toString()
                }
            }) { Text("تست همه مدل‌ها") }

            Button(onClick = {
                // Trigger recomposition: simple no-op hint
            }) { Text("Refresh") }
        }
        Text(testResults, style = MaterialTheme.typography.bodySmall)
    }
}
