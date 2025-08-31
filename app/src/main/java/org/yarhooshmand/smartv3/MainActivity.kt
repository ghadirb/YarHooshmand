
package org.yarhooshmand.smartv3

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import org.yarhooshmand.smartv3.ui.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestNoti = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        val requestMic = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        if (Build.VERSION.SDK_INT >= 33) requestNoti.launch(Manifest.permission.POST_NOTIFICATIONS)
        requestMic.launch(Manifest.permission.RECORD_AUDIO)

        setContent { MaterialTheme { AppNav() } }
    }
}
