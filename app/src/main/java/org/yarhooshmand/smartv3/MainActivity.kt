package org.yarhooshmand.smartv3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import org.yarhooshmand.smartv3.ui.AppNav
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: ReminderViewModel = viewModel()
            AppNav(viewModel = vm)
        }
    }
}
