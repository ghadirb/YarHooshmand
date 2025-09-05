package org.yarhooshmand.smartv3.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yarhooshmand.smartv3.ai.ProviderType
import org.yarhooshmand.smartv3.ai.ModelAllowlist
import org.yarhooshmand.smartv3.utils.AISettingsPref
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(ctx: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("تنظیمات", style = MaterialTheme.typography.headlineSmall)

        // بخش انتخاب تم
        AppearanceSection()

        // بخش مدل‌های فعال
        ActiveModelsSection(ctx)

        // بخش بک‌آپ و گوگل درایو (اختیاری، برای نمونه)
        BackupSection()
    }
}

@Composable
fun AppearanceSection() {
    var isDark by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("انتخاب تم", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = !isDark,
                onClick = { isDark = false },
                label = { Text("روشن") }
            )
            FilterChip(
                selected = isDark,
                onClick = { isDark = true },
                label = { Text("تاریک") }
            )
        }

        Text(
            "تغییر تم در لحظه روی صفحه‌ها اعمال می‌شود.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ActiveModelsSection(ctx: Context) {
    Text("مدل‌های فعال", style = MaterialTheme.typography.titleMedium)

    val providers = listOf(
        ProviderType.OPENAI to ModelAllowlist.openAi,
        ProviderType.OPENROUTER to ModelAllowlist.openRouter,
        ProviderType.ANTHROPIC to ModelAllowlist.anthropic
    )

    val active = remember {
        mutableStateListOf<Pair<ProviderType, org.yarhooshmand.smartv3.ai.ModelDescriptor>>()
    }

    LaunchedEffect(Unit) {
        active.clear()
        providers.forEach { (p, list) ->
            val key = AISettingsPref.getApiKey(ctx, p)
            if (!key.isNullOrBlank()) {
                val sel = AISettingsPref.getModel(ctx, p) ?: list.first().id
                val md = list.firstOrNull { it.id == sel } ?: list.first()
                active += p to md
            }
        }
    }

    var defaultProv by remember { mutableStateOf(AISettingsPref.getDefaultProvider(ctx)) }
    var testResults by remember { mutableStateOf("—") }
    val scope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        active.forEach { (p, md) ->
            ElevatedCard {
                Row(
                    Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(md.display, style = MaterialTheme.typography.titleSmall)
                        Text(p.name, style = MaterialTheme.typography.bodySmall)
                    }
                    val selected = defaultProv == p
                    AssistChip(
                        onClick = {
                            defaultProv = p
                            AISettingsPref.setDefaultProvider(ctx, p)
                        },
                        label = { Text(if (selected) "فعال ✅" else "انتخاب") }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                scope.launch {
                    val svc = org.yarhooshmand.smartv3.ai.AIService(ctx)
                    val sb = StringBuilder()
                    active.forEach { (p, _) ->
                        val r = svc.chat("سلام")
                        sb.append(p.name).append(": ")
                        if (r.isSuccess) {
                            sb.append("موفق ✅\n")
                        } else {
                            sb.append("خطا ❌ ").append(r.exceptionOrNull()?.message).append("\n")
                        }
                    }
                    testResults = sb.toString()
                }
            }) {
                Text("تست همه مدل‌ها")
            }

            Button(onClick = {
                // فقط Refresh برای بازخوانی مدل‌ها
                active.clear()
                providers.forEach { (p, list) ->
                    val key = AISettingsPref.getApiKey(ctx, p)
                    if (!key.isNullOrBlank()) {
                        val sel = AISettingsPref.getModel(ctx, p) ?: list.first().id
                        val md = list.firstOrNull { it.id == sel } ?: list.first()
                        active += p to md
                    }
                }
            }) {
                Text("Refresh")
            }
        }

        Text(testResults, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BackupSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("بک‌آپ و بازیابی", style = MaterialTheme.typography.titleMedium)
        Button(onClick = {
            // TODO: اتصال به Google Drive برای بک‌آپ
        }) {
            Text("بک‌آپ روی Google Drive")
        }
        Button(onClick = {
            // TODO: بازیابی بک‌آپ از Google Drive
        }) {
            Text("بازیابی از Google Drive")
        }
    }
}
