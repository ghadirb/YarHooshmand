package org.yarhooshmand.smartv3.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.yarhooshmand.smartv3.data.ReminderRepository

@Composable
fun AppNav() {
    val ctx = LocalContext.current
    val repo = remember { ReminderRepository.getInstance(ctx) }
    RemindersScreen(repository = repo) { }
}
