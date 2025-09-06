package org.yarhooshmand.smartv3.utils

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SecureVault {
    private const val FILE = "secure_vault"
    private const val KEY_MASTER = "master_set" // flag that master is set

    fun isMasterSet(ctx: Context): Boolean {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_MASTER, false)
    }

    fun setMasterSet(ctx: Context) {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        sp.edit().putBoolean(KEY_MASTER, true).apply()
    }

    fun encryptToBase64(password: String, plaintext: ByteArray): String {
        val sr = SecureRandom()
        val salt = ByteArray(16).also { sr.nextBytes(it) }
        val iv = ByteArray(12).also { sr.nextBytes(it) }
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val ct = cipher.doFinal(plaintext)
        val out = ByteArray(salt.size + iv.size + ct.size)
        System.arraycopy(salt, 0, out, 0, salt.size)
        System.arraycopy(iv, 0, out, salt.size, iv.size)
        System.arraycopy(ct, 0, out, salt.size + iv.size, ct.size)
        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    fun decryptFromBase64(password: String, b64: String): ByteArray {
        val data = Base64.decode(b64, Base64.DEFAULT)
        val salt = data.copyOfRange(0, 16)
        val iv = data.copyOfRange(16, 28)
        val ct = data.copyOfRange(28, data.size)
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(ct)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, 20000, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = skf.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }
}
