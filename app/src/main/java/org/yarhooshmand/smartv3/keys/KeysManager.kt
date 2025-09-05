package org.yarhooshmand.smartv3.keys

import android.content.Context
import org.json.JSONObject
import org.yarhooshmand.smartv3.ai.ProviderType
import org.yarhooshmand.smartv3.utils.AISettingsPref
import org.yarhooshmand.smartv3.utils.SecureVault

/**
 * KeysManager
 *
 * وظیفه:
 * - وارد کردن کلیدها از فایل رمزگذاری‌شده (Base64 | salt+iv+cipher) با پسورد
 * - خروجی گرفتن کلیدهای فعلی به همان فرمت رمزگذاری‌شده
 * - ست/گت ساده‌ی کلیدهای Provider از/به AISettingsPref
 *
 * نکته امنیتی:
 * این کلاس پسورد را ذخیره نمی‌کند. برای Import/Export هر بار پسورد دریافت می‌شود.
 * در زمان Import، کلیدها به AISettingsPref ست می‌شوند (برای استفاده اپ).
 */
class KeysManager(
    private val context: Context,
    private val vault: SecureVault = SecureVault()
) {

    /**
     * ذخیره کلید ساده برای یک Provider (بدون رمزگذاری در این مرحله).
     * اگر می‌خواهی همیشه رمز شود، می‌توانیم AISettingsPref را به SecureVault وصل کنیم.
     */
    fun savePlainKey(provider: ProviderType, apiKey: String?) {
        AISettingsPref.setApiKey(context, provider, apiKey ?: "")
    }

    /**
     * دریافت کلید ساده از AISettingsPref
     */
    fun getPlainKey(provider: ProviderType): String? {
        return AISettingsPref.getApiKey(context, provider)
    }

    /**
     * Import از رشته Base64 رمزگذاری‌شده (salt+iv+cipher) با پسورد.
     * قالب JSON داخل ciphertext به‌صورت مثال:
     * {
     *   "OPENAI": { "apiKey": "sk-..." },
     *   "OPENROUTER": { "apiKey": "..." },
     *   "ANTHROPIC": { "apiKey": "..." }
     * }
     *
     * خروجی: Result<Int> = تعداد کلیدهای ست‌شده
     */
    fun importFromEncryptedBase64(encryptedBase64: String, password: CharArray): Result<Int> {
        return try {
            val plainBytes = vault.decrypt(encryptedBase64, password)
            val json = JSONObject(String(plainBytes, Charsets.UTF_8))

            var setCount = 0
            ProviderType.values().forEach { p ->
                val obj = json.optJSONObject(p.name)
                val key = obj?.optString("apiKey").orEmpty()
                if (key.isNotBlank()) {
                    AISettingsPref.setApiKey(context, p, key)
                    setCount++
                }
            }
            Result.success(setCount)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * Export کلیدهای فعلی (موجود در AISettingsPref) به Base64 رمزگذاری‌شده.
     * همان فرمت JSON بالا را می‌سازد و با PBKDF2+AES-GCM رمز می‌کند.
     */
    fun exportToEncryptedBase64(password: CharArray): Result<String> {
        return try {
            val root = JSONObject()
            ProviderType.values().forEach { p ->
                val key = AISettingsPref.getApiKey(context, p).orEmpty()
                if (key.isNotBlank()) {
                    val obj = JSONObject().put("apiKey", key)
                    root.put(p.name, obj)
                }
            }
            val plain = root.toString().toByteArray(Charsets.UTF_8)
            val encB64 = vault.encrypt(plain, password)
            Result.success(encB64)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * فعال بودن یک Provider بر اساس داشتن کلید غیرخالی
     */
    fun isProviderActive(provider: ProviderType): Boolean {
        return !AISettingsPref.getApiKey(context, provider).isNullOrBlank()
    }

    /**
     * فعال‌ها را برمی‌گرداند (برای لیست مدل‌های فعال در UI)
     */
    fun getActiveProviders(): List<ProviderType> {
        return ProviderType.values().filter { isProviderActive(it) }
    }
}
