package org.yarhooshmand.smartv3.models

import android.content.Context
import com.google.gson.Gson

data class ModelStatus(val name: String, var enabled: Boolean, var online: Boolean = true)

object ModelManager {
    private const val PREFS = "yar_models_prefs"
    private const val MODELS = "models"
    private const val SIMPLE = "simple_mode"
    private const val LISTEN = "listen_after_alarm"
    private val gson = Gson()
    private var cache: MutableList<ModelStatus> = mutableListOf()

    fun init(ctx: Context) {
        if (cache.isEmpty()) {
            val saved = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(MODELS, null)
            cache = if (saved.isNullOrBlank()) {
                mutableListOf(
                    ModelStatus("gpt-4o-mini", true),
                    ModelStatus("gpt-4o", false),
                    ModelStatus("gpt-3.5-turbo", false)
                )
            } else {
                gson.fromJson(saved, Array<ModelStatus>::class.java).toMutableList()
            }
            persist(ctx)
        }
    }

    private fun persist(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(MODELS, gson.toJson(cache)).apply()
    }

    fun all(): List<ModelStatus> = cache

    fun setEnabled(ctx: Context, name: String, enabled: Boolean) {
        cache.find { it.name == name }?.enabled = enabled
        persist(ctx)
    }

    fun firstEnabled(): String? = cache.firstOrNull { it.enabled }?.name

    fun isSimpleMode(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(SIMPLE, true)
    fun setSimpleMode(ctx: Context, v: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(SIMPLE, v).apply()
    }

    fun isListenAfterAlarm(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(LISTEN, false)
    fun setListenAfterAlarm(ctx: Context, v: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(LISTEN, v).apply()
    }
}
