package com.yarhooshmand.sms

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object SmsHelper {
    private const val PERM = Manifest.permission.SEND_SMS
    private const val REQ = 101

    fun ensurePermission(activity: Activity): Boolean {
        val granted = ContextCompat.checkSelfPermission(activity, PERM) == PackageManager.PERMISSION_GRANTED
        if (!granted) ActivityCompat.requestPermissions(activity, arrayOf(PERM), REQ)
        return granted
    }

    fun send(phone: String, message: String) {
        try {
            SmsManager.getDefault().sendTextMessage(phone, null, message, null, null)
        } catch (_: Exception) { }
    }
}
