package com.yarhooshmand.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yarhooshmand.ui.ReminderViewModel
import java.util.*

@Composable
fun AddReminderScreen(viewModel: ReminderViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = title, onValueChange = { title = it },
            label = { Text("عنوان یادآور") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = {
            val c = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d -> date = "%04d-%02d-%02d".format(y, m + 1, d) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }) { Text(if (date.isEmpty()) "انتخاب تاریخ" else date) }

        Button(onClick = {
            val c = Calendar.getInstance()
            TimePickerDialog(context, { _, h, min -> time = "%02d:%02d".format(h, min) },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }) { Text(if (time.isEmpty()) "انتخاب ساعت" else time) }

        Button(onClick = {
            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                viewModel.addReminder(context, title, date, time)
                title = ""; date = ""; time = ""
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("ثبت یادآور") }
    }
}
