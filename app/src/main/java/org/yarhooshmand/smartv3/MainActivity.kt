package org.yarhooshmand.smartv3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import org.yarhooshmand.smartv3.ui.AppRoot
import org.yarhooshmand.smartv3.ui.theme.SmartReminderTheme

/**
 * MainActivity
 * نقطه شروع برنامه
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartReminderTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppRoot()   // کل UI اپ از اینجا شروع میشه
                }
            }
        }
    }
}
