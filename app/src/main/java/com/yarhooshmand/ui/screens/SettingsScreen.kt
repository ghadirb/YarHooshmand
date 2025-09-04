package com.yarhooshmand.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.yarhooshmand.security.Crypto
import com.yarhooshmand.security.KeysManager
import com.yarhooshmand.ui.theme.ThemeController
import com.yarhooshmand.drive.DriveBackupHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    var phonePrimary by remember { mutableStateOf(prefs.getString("phone_number", "") ?: "") }
    var smsPrimary by remember { mutableStateOf(prefs.getBoolean("sms_enabled", false)) }

    var phoneSecondary by remember { mutableStateOf(prefs.getString("phone_number_secondary", "") ?: "") }
    var smsSecondary by remember { mutableStateOf(prefs.getBoolean("sms_secondary_enabled", false)) }

    var activeModel by remember { mutableStateOf(prefs.getString("active_model", "gpt-4o-mini") ?: "gpt-4o-mini") }
    var modelsEnabled by remember { mutableStateOf(prefs.getBoolean("models_enabled", true)) }

    var encB64 by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var decryptStatus by remember { mutableStateOf("") }
    var plainKeys by remember { mutableStateOf(KeysManager.getDecrypted(context) ?: "") }

    var driveEmail by remember { mutableStateOf(GoogleSignIn.getLastSignedInAccount(context)?.email ?: "") }
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val acc: GoogleSignInAccount = task.result
            driveEmail = acc.email ?: ""
        } catch (_: Exception) { }
    }

    fun launchSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        signInLauncher.launch(client.signInIntent)
    }

    var themeMode by remember { mutableStateOf(ThemeController.mode) }
    val scope = remember { CoroutineScope(Dispatchers.IO) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Google Drive", style = MaterialTheme.typography.titleMedium)
        if (driveEmail.isEmpty()) {
            Button(onClick = { launchSignIn() }, modifier = Modifier.fillMaxWidth()) { Text("ورود به گوگل درایو") }
        } else {
            Text("وارد شده به: $driveEmail")
            Button(onClick = {
                val client = GoogleSignIn.getClient(context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                client.signOut()
                driveEmail = ""
            }, modifier = Modifier.fillMaxWidth()) { Text("خروج از گوگل") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    val dir = context.getExternalFilesDir(null) ?: return@launch
                    val latest = dir.listFiles()?.filter { it.name.startsWith("backup_") }?.maxByOrNull { it.lastModified() }
                    val f = latest ?: File(dir, "manual_backup.json").apply { writeText("[]") }
                    val id = DriveBackupHelper(context).uploadJson(f)
                    launch(Dispatchers.Main) { Toast.makeText(context, if (id!=null) "آپلود شد: $id" else "ابتدا وارد گوگل شوید", Toast.LENGTH_SHORT).show() }
                }
            }, modifier = Modifier.weight(1f)) { Text("آپلود فوری به درایو") }
            Button(onClick = {
                scope.launch {
                    val dir = context.getExternalFilesDir(null) ?: return@launch
                    val dest = File(dir, "restore_latest.json")
                    val ok = DriveBackupHelper(context).downloadLatest(dest)
                    launch(Dispatchers.Main) { Toast.makeText(context, if (ok) "بازیابی ذخیره شد: ${dest.name}" else "فایلی یافت نشد یا وارد نشده‌اید", Toast.LENGTH_SHORT).show() }
                }
            }, modifier = Modifier.weight(1f)) { Text("بازیابی از درایو") }
        }

        Divider()
        Text("SMS", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = phonePrimary, onValueChange = { phonePrimary = it }, label = { Text("شماره اصلی") }, modifier = Modifier.fillMaxWidth())
        Row { Checkbox(checked = smsPrimary, onCheckedChange = { smsPrimary = it }); Text("ارسال به شماره اصلی", modifier = Modifier.padding(start = 8.dp)) }
        OutlinedTextField(value = phoneSecondary, onValueChange = { phoneSecondary = it }, label = { Text("شماره اشتراکی (اختیاری)") }, modifier = Modifier.fillMaxWidth())
        Row { Checkbox(checked = smsSecondary, onCheckedChange = { smsSecondary = it }); Text("ارسال به شماره اشتراکی", modifier = Modifier.padding(start = 8.dp)) }

        Divider()
        Text("مدل‌ها", style = MaterialTheme.typography.titleMedium)
        Row { Checkbox(checked = modelsEnabled, onCheckedChange = { modelsEnabled = it }); Text("فعال بودن مدل‌ها") }
        OutlinedTextField(value = activeModel, onValueChange = { activeModel = it }, label = { Text("نام مدل فعال") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { /* Refresh models stub */ }, modifier = Modifier.fillMaxWidth()) { Text("رفرش مدل‌ها") }

        Divider()
        Text("تم برنامه", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = themeMode==ThemeController.Mode.SYSTEM, onClick = { themeMode = ThemeController.Mode.SYSTEM; ThemeController.save(context, themeMode) }, label = { Text("سیستمی") })
            FilterChip(selected = themeMode==ThemeController.Mode.LIGHT, onClick = { themeMode = ThemeController.Mode.LIGHT; ThemeController.save(context, themeMode) }, label = { Text("روشن") })
            FilterChip(selected = themeMode==ThemeController.Mode.DARK, onClick = { themeMode = ThemeController.Mode.DARK; ThemeController.save(context, themeMode) }, label = { Text("تاریک") })
        }

        Divider()
        Text("کلیدها (Base64 رمزنگاری‌شده)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = encB64, onValueChange = { encB64 = it }, label = { Text("محتوای Base64 فایل کلیدها") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("رمز عبور (مثلاً 12345)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            try {
                val bytes = Crypto.decryptBase64(encB64.trim(), password)
                val text = String(bytes, Charsets.UTF_8)
                KeysManager.saveDecrypted(context, text)
                plainKeys = text
                decryptStatus = "کلیدها با موفقیت باز شدند."
            } catch (e: Exception) {
                decryptStatus = "خطا در رمزگشایی: " + (e.message ?: "")
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("رمزگشایی و ذخیره") }
        if (decryptStatus.isNotEmpty()) Text(decryptStatus, color = MaterialTheme.colorScheme.primary)
        if (plainKeys.isNotEmpty()) OutlinedTextField(value = plainKeys, onValueChange = {}, label = { Text("کلیدهای رمزگشایی‌شده") }, modifier = Modifier.fillMaxWidth(), readOnly = true)

        Divider()
        Button(onClick = {
            prefs.edit()
                .putString("phone_number", phonePrimary)
                .putBoolean("sms_enabled", smsPrimary)
                .putString("phone_number_secondary", phoneSecondary)
                .putBoolean("sms_secondary_enabled", smsSecondary)
                .putBoolean("models_enabled", modelsEnabled)
                .putString("active_model", activeModel)
                .apply()
            Toast.makeText(context, "ذخیره شد", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth()) { Text("ذخیره تنظیمات") }
    }
}
