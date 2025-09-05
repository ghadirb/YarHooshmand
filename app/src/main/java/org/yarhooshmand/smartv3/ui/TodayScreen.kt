package org.yarhooshmand.smartv3.ui

import androidx.compose.runtime.Composable
import org.yarhooshmand.smartv3.reminders.ReminderRepository
import org.yarhooshmand.smartv3.ui.RemindersUX

@Composable
fun TodayScreen(repo: ReminderRepository) {
    RemindersUX(repo = repo)
}
