package com.yarhooshmand.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object Crypto {
    fun decryptBase64(b64: String, password: String): ByteArray {
        val data = Base64.decode(b64, Base64.DEFAULT)
        val salt = data.copyOfRange(0, 16)
        val iv = data.copyOfRange(16, 28)
        val ct = data.copyOfRange(28, data.size)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 20000, 256)
        val tmp = factory.generateSecret(spec)
        val key = SecretKeySpec(tmp.encoded, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val specGcm = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, specGcm)
        return cipher.doFinal(ct)
    }
}
