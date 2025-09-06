package org.yarhooshmand.smartv3.keys

import android.content.Context

object PassphraseManager {
    private const val PREFS = "yar_keys_prefs"
    private const val PASS = "keys_passphrase"

    fun get(ctx: Context): String? {
        return try {
            android.security.keystore.KeyProperties::class
            val sp = androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sp.getString(PASS, null)
        } catch (e: Exception) {
            val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            sp.getString(PASS, null)
        }
    }

    fun set(ctx: Context, pass: String) {
        try {
            android.security.keystore.KeyProperties::class
            val sp = androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sp.edit().putString(PASS, pass).apply()
        } catch (e: Exception) {
            val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            sp.edit().putString(PASS, pass).apply()
        }
    }
}
