package org.yarhooshmand.smartv3.keys

import android.content.Context
import android.util.Base64

object SecureVault {
    fun encrypt(bytes: ByteArray): ByteArray = Base64.encode(bytes, Base64.NO_WRAP)
    fun decrypt(bytes: ByteArray): ByteArray = Base64.decode(bytes, Base64.NO_WRAP)
}

object KeysManager {

    private const val PREF = "keys_prefs"
    private const val KEY_ACTIVE = "active_key"

    fun getActiveKey(ctx: Context): String? =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_ACTIVE, null)

    fun getActiveKeySafe(ctx: Context): String? = getActiveKey(ctx)

    fun setActiveKey(ctx: Context, rawKey: String) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(KEY_ACTIVE, rawKey).apply()
    }

    fun reportBadKeyInternal(ctx: Context, reason: String) {
        // no-op
    }
}
