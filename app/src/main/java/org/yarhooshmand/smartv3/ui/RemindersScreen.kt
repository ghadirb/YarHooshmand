package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel
import org.yarhooshmand.smartv3.data.ReminderEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel = viewModel()
) {
    val reminders by reminderViewModel.reminders.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("یادآورها") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: افزودن یادآور */ }) { Text("+") }
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("هیچ یادآوری ثبت نشده") }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(reminders) { r: ReminderEntity ->
                    ListItem(
                        headlineContent = { Text("یادآور #${r.id}") },
                        supportingContent = { Text(r.toString()) }
                    )
                    Divider()
                }
            }
        }
    }
}
