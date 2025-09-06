
package org.yarhooshmand.smartv3.utils

import android.content.Context
import android.content.SharedPreferences

object SmsPrefs {
    private const val PREFS = "yar_sms_prefs"
    private const val NUMBER = "default_number"
    private const val MASTER_ENABLE = "master_enable"

    private fun sp(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isMasterEnabled(ctx: Context): Boolean = sp(ctx).getBoolean(MASTER_ENABLE, false)
    fun setMasterEnabled(ctx: Context, enabled: Boolean) {
        sp(ctx).edit().putBoolean(MASTER_ENABLE, enabled).apply()
    }

    fun getDefaultNumber(ctx: Context): String = sp(ctx).getString(NUMBER, "") ?: ""
    fun setDefaultNumber(ctx: Context, number: String) {
        sp(ctx).edit().putString(NUMBER, number).apply()
    }

    fun isEnabledForReminder(ctx: Context, id: Long): Boolean =
        sp(ctx).getBoolean("sms_"+id, false)

    fun setEnabledForReminder(ctx: Context, id: Long, enabled: Boolean) {
        sp(ctx).edit().putBoolean("sms_"+id, enabled).apply()
    }
}
