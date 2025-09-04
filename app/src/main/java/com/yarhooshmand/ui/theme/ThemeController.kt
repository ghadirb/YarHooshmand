package com.yarhooshmand.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeController {
    enum class Mode { SYSTEM, LIGHT, DARK }
    private var _mode by mutableStateOf(Mode.SYSTEM)
    val mode get() = _mode

    fun load(context: Context) {
        val v = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        _mode = try { Mode.valueOf(v) } catch (_: Exception) { Mode.SYSTEM }
    }
    fun save(context: Context, mode: Mode) {
        _mode = mode
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putString("theme_mode", mode.name).apply()
    }
}
