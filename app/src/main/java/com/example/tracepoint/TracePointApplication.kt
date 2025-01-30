package com.example.tracepoint

import android.app.Application
import com.example.tracepoint.utils.SharedPrefsManager

class TracePointApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefsManager.init(this)
    }
}
