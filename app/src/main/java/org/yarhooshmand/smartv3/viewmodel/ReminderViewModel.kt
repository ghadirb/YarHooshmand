package org.yarhooshmand.smartv3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderRepository
import org.yarhooshmand.smartv3.data.ReminderEntity

class ReminderViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ReminderRepository.get(app)

    val reminders = repo.allReminders
        .map { it.sortedWith(compareBy(nullsLast()) { it.date }) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(title: String, note: String? = null, date: Long? = null) = viewModelScope.launch {
        repo.add(title, note, date)
    }

    fun toggle(id: Long) = viewModelScope.launch {
        repo.toggleDone(id)
    }

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
    }

    fun update(entity: ReminderEntity) = viewModelScope.launch {
        repo.update(entity)
    }
}
