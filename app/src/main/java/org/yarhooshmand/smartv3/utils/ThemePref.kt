
package org.yarhooshmand.smartv3.utils

import android.content.Context

object ThemePref {
    private const val PREFS = "yar_ui_prefs"
    private const val KEY_DARK = "dark_mode"
    fun isDark(ctx: Context): Boolean {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_DARK, false)
    }
    fun setDark(ctx: Context, v: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_DARK, v).apply()
    }
}
