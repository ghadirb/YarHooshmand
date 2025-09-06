package org.yarhooshmand.smartv3.utils

import android.content.Context

/**
 * نسخه‌ی ساده و Null-safe از GoogleDriveHelper فقط برای جلوگیری از خطای کامپایل.
 * در صورت نیاز می‌توانی با پیاده‌سازی اصلی جایگزینش کنی.
 */
class GoogleDriveHelper(private val context: Context) {

    fun signIn(clientId: String?): Result<Unit> {
        // جلوگیری از Type mismatch: اگر null بود، خطا برمی‌گردانیم اما کامپایل می‌شود.
        return if (clientId.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("clientId is null/blank"))
        } else {
            Result.success(Unit)
        }
    }

    fun backup(fileName: String?, bytes: ByteArray): Result<Unit> {
        return if (fileName.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("fileName is null/blank"))
        } else {
            // TODO: آپلود واقعی؛ در حال حاضر فقط موفق گزارش می‌کند
            Result.success(Unit)
        }
    }

    fun restore(fileName: String?): Result<ByteArray> {
        return if (fileName.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("fileName is null/blank"))
        } else {
            // TODO: دانلود واقعی؛ در حال حاضر داده خالی
            Result.success(ByteArray(0))
        }
    }
}
