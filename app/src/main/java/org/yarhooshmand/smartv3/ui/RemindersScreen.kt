package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.yarhooshmand.smartv3.data.Reminder
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@Composable
fun RemindersScreen(
    navController: NavController,
    viewModel: ReminderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val reminders = viewModel.reminders.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("یادآورها") }
            )
        }
    ) { padding ->
        if (reminders.value.isEmpty()) {
            Text(
                text = "هیچ یادآوری ثبت نشده",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reminders.value) { reminder: Reminder ->
                    ListItem(
                        headlineText = { Text(reminder.title) },
                        supportingText = { Text(reminder.description ?: "") }
                    )
                    Divider()
                }
            }
        }
    }
}
