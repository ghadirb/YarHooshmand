package org.yarhooshmand.smartv3

import android.app.Application
import android.content.Context
import org.yarhooshmand.smartv3.data.AppDatabase

class YarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        // Warm-up DB instance
        AppDatabase.getInstance(this)
    }

    companion object {
        @Volatile private var appContext: Context? = null
        fun context(): Context = appContext
            ?: throw IllegalStateException("YarApp not initialized")
    }
}
