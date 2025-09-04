package com.yarhooshmand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarhooshmand.data.ReminderDatabase
import com.yarhooshmand.data.ReminderRepository
import com.yarhooshmand.notification.NotificationHelper
import com.yarhooshmand.ui.ReminderViewModel
import com.yarhooshmand.ui.ReminderViewModelFactory
import com.yarhooshmand.ui.screens.AppNavHost
import com.yarhooshmand.ui.theme.YarHooshmandTheme
import com.yarhooshmand.sms.SmsHelper
import com.yarhooshmand.ui.theme.ThemeController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        SmsHelper.ensurePermission(this)
        ThemeController.load(this)

        val dao = ReminderDatabase.getDatabase(this).reminderDao()
        val repository = ReminderRepository(dao)
        val factory = ReminderViewModelFactory(repository)

        setContent {
            YarHooshmandTheme {
                val vm: ReminderViewModel = viewModel(factory = factory)
                AppNavHost(vm)
            }
        }
    }
}
