package com.yarhooshmand.security

import android.content.Context

object KeysManager {
    private const val PREF = "keys"
    fun saveDecrypted(context: Context, plaintext: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putString("keys_plain", plaintext).apply()
    }
    fun getDecrypted(context: Context): String? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString("keys_plain", null)
}
