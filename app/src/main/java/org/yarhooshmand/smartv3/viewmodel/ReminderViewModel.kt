package org.yarhooshmand.smartv3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.data.ReminderRepository

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository = ReminderRepository.getInstance(application)

    private val _reminders = MutableStateFlow<List<ReminderEntity>>(emptyList())
    val reminders: StateFlow<List<ReminderEntity>> = _reminders.asStateFlow()

    init {
        viewModelScope.launch {
            // اگر ریپازیتوری Flow برمی‌گرداند:
            repository.getAllReminders().collect { list ->
                _reminders.value = list
            }
            // اگر Flow ندارید، می‌توانید در ریپازیتوری متدی مثل getAllOnce() داشته باشید
            // و همینجا مقداردهی کنید.
        }
    }

    fun addReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.insert(reminder) }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.delete(reminder) }
    }
}
