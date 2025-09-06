package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DashboardScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {}) { Text("Create Backup") }
        Button(onClick = {}) { Text("Restore Backup") }
        Button(onClick = {}) { Text("Schedule Auto Backup") }
    }
}
