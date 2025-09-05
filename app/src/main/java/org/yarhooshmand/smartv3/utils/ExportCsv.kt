package org.yarhooshmand.smartv3.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset

/**
 * ExportCsv
 *
 * ابزار خروجی گرفتن CSV به‌شکل امن و بدون خطای نحوی.
 * توابع:
 *  - toCsv(...) : ساخت رشته CSV
 *  - exportToUri(...) : نوشتن روی Uri (مثلاً فایلی که کاربر از طریق SAF انتخاب کرده)
 *  - exportToDownloads(...) : ذخیره در Downloads با MediaStore (Android Q+)، یا مسیر app در قدیمی‌تر
 */
object ExportCsv {

    /**
     * تبدیل هدر/سطرها به CSV معتبر (UTF-8).
     * کاراکترهای " داخل سلول با "" فرار داده می‌شوند و کل سلول در " قرار می‌گیرد.
     */
    fun toCsv(
        headers: List<String>,
        rows: List<List<String>>
    ): String {
        val sb = StringBuilder()

        fun esc(cell: String): String {
            val needQuote = cell.contains(',') || cell.contains('"') || cell.contains('\n') || cell.contains('\r')
            val c = cell.replace("\"", "\"\"")
            return if (needQuote) "\"$c\"" else c
        }

        if (headers.isNotEmpty()) {
            sb.append(headers.joinToString(",") { esc(it) }).append("\n")
        }
        rows.forEach { r ->
            sb.append(r.joinToString(",") { esc(it) }).append("\n")
        }
        return sb.toString()
    }

    /**
     * نوشتن CSV روی یک Uri (مثلاً فایلی که با SAF انتخاب شده)
     */
    fun exportToUri(
        context: Context,
        dest: Uri,
        headers: List<String>,
        rows: List<List<String>>,
        charset: Charset = Charsets.UTF_8
    ): Result<Unit> {
        return try {
            val csv = toCsv(headers, rows)
            context.contentResolver.openOutputStream(dest, "w").use { os ->
                if (os == null) return Result.failure(IllegalStateException("Cannot open output stream"))
                OutputStreamWriter(os, charset).use { ow ->
                    BufferedWriter(ow).use { bw ->
                        bw.write(csv)
                        bw.flush()
                    }
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * ذخیره CSV در Downloads (Android 10+ با MediaStore)،
     * در نسخه‌های قدیمی‌تر در پوشه‌ی اختصاصی اپ داخل external files.
     *
     * خروجی: Uri فایل ذخیره‌شده (یا Failure)
     */
    fun exportToDownloads(
        context: Context,
        fileName: String,
        headers: List<String>,
        rows: List<List<String>>,
        mime: String = "text/csv",
        charset: Charset = Charsets.UTF_8
    ): Result<Uri> {
        return try {
            val csv = toCsv(headers, rows)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName.ensureCsvExtension())
                    put(MediaStore.Downloads.MIME_TYPE, mime)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val itemUri = resolver.insert(collection, values)
                    ?: return Result.failure(IllegalStateException("Failed to create file in MediaStore"))

                resolver.openOutputStream(itemUri, "w").use { os ->
                    if (os == null) return Result.failure(IllegalStateException("Cannot open output stream"))
                    OutputStreamWriter(os, charset).use { ow ->
                        BufferedWriter(ow).use { bw ->
                            bw.write(csv)
                            bw.flush()
                        }
                    }
                }

                // mark not pending
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(itemUri, values, null, null)

                Result.success(itemUri)
            } else {
                // Legacy path: /storage/emulated/0/Android/data/<pkg>/files/Download/
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: context.filesDir
                val file = File(dir, fileName.ensureCsvExtension())
                FileOutputStream(file).use { fos ->
                    OutputStreamWriter(fos, charset).use { ow ->
                        BufferedWriter(ow).use { bw ->
                            bw.write(csv)
                            bw.flush()
                        }
                    }
                }
                Result.success(Uri.fromFile(file))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun String.ensureCsvExtension(): String {
        return if (lowercase().endsWith(".csv")) this else "$this.csv"
    }
}
