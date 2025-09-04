package com.yarhooshmand.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yarhooshmand.data.Reminder
import com.yarhooshmand.data.ReminderRepository
import com.yarhooshmand.util.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {
    val reminders: StateFlow<List<Reminder>> =
        repository.allReminders.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addReminder(context: Context, title: String, date: String, time: String) {
        viewModelScope.launch {
            val reminder = Reminder(title = title, date = date, time = time)
            val id = repository.insert(reminder).toInt()
            AlarmScheduler.schedule(context, date, time, title, id)
        }
    }
    fun deleteReminder(reminder: Reminder) = viewModelScope.launch { repository.delete(reminder) }
    fun toggleDone(reminder: Reminder) = viewModelScope.launch { repository.update(reminder.copy(isDone = !reminder.isDone)) }
    fun deleteAll() = viewModelScope.launch { repository.deleteAll() }
}

@Suppress("UNCHECKED_CAST")
class ReminderViewModelFactory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
