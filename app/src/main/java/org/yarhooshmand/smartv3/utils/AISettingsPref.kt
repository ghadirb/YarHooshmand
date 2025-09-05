package org.yarhooshmand.smartv3.utils

import android.content.Context
import org.yarhooshmand.smartv3.ai.ProviderType

object AISettingsPref {
    private const val FILE = "ai_settings"
    private const val KEY_OPENAI = "api_openai"
    private const val KEY_OPENROUTER = "api_openrouter"
    private const val KEY_ANTHROPIC = "api_anthropic"
    private const val KEY_DEFAULT_PROVIDER = "default_provider"
    private const val KEY_FALLBACK = "fallback_enabled"
    private const val KEY_MODEL_OPENAI = "model_openai"
    private const val KEY_MODEL_OPENROUTER = "model_openrouter"
    private const val KEY_MODEL_ANTHROPIC = "model_anthropic"

    fun setApiKey(ctx: Context, provider: ProviderType, key: String) {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val k = when (provider) { ProviderType.OPENAI -> KEY_OPENAI; ProviderType.OPENROUTER -> KEY_OPENROUTER; ProviderType.ANTHROPIC -> KEY_ANTHROPIC }
        sp.edit().putString(k, key).apply()
    }

    fun getApiKey(ctx: Context, provider: ProviderType): String? {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val k = when (provider) { ProviderType.OPENAI -> KEY_OPENAI; ProviderType.OPENROUTER -> KEY_OPENROUTER; ProviderType.ANTHROPIC -> KEY_ANTHROPIC }
        return sp.getString(k, null)
    }

    fun setDefaultProvider(ctx: Context, provider: ProviderType) {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_DEFAULT_PROVIDER, provider.name).apply()
    }

    fun getDefaultProvider(ctx: Context): ProviderType {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val n = sp.getString(KEY_DEFAULT_PROVIDER, ProviderType.OPENAI.name)!!
        return ProviderType.valueOf(n)
    }

    fun setFallbackEnabled(ctx: Context, enabled: Boolean) {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        sp.edit().putBoolean(KEY_FALLBACK, enabled).apply()
    }

    fun isFallbackEnabled(ctx: Context): Boolean {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_FALLBACK, true)
    }

    fun setModel(ctx: Context, provider: ProviderType, modelId: String) {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val k = when (provider) { ProviderType.OPENAI -> KEY_MODEL_OPENAI; ProviderType.OPENROUTER -> KEY_MODEL_OPENROUTER; ProviderType.ANTHROPIC -> KEY_MODEL_ANTHROPIC }
        sp.edit().putString(k, modelId).apply()
    }

    fun getModel(ctx: Context, provider: ProviderType): String? {
        val sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val k = when (provider) { ProviderType.OPENAI -> KEY_MODEL_OPENAI; ProviderType.OPENROUTER -> KEY_MODEL_OPENROUTER; ProviderType.ANTHROPIC -> KEY_MODEL_ANTHROPIC }
        return sp.getString(k, null)
    }
}
