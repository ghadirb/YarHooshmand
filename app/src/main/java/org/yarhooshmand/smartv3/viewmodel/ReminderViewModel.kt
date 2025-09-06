package org.yarhooshmand.smartv3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderRepository
import org.yarhooshmand.smartv3.data.local.Reminder

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    val allReminders: LiveData<List<Reminder>> = repository.allReminders

    fun insert(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
    }

    suspend fun getReminderById(id: Long): Reminder? {
        return repository.getByIdOnce(id)
    }
}
