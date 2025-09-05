package org.yarhooshmand.smartv3

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.ui.AppNav
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.keys.KeysManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request runtime permissions early
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.SEND_SMS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // On older versions, POST_NOTIFICATIONS may not be required
        }

        val request = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            // no-op; permissions handled where needed
        }
        request.launch(permissions.toTypedArray())

        // Initialize keys in background
        try {
            KeysManager.init(applicationContext)
        } catch (_: Exception) {}

        setContent { org.yarhooshmand.smartv3.ui.AppRoot() }
        }
    }
}
