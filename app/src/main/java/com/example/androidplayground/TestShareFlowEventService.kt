package com.example.androidplayground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import timber.log.Timber

class TestShareFlowEventService : Service() {

    private val scope = MainScope() + CoroutineName("TestShareFlowEventService_Scope")

    override fun onBind(intent: Intent) = null

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            AppEventCenter.subscribe<ServiceEvent> {
                try {
                    delay(2000)
                    Timber.d("收到: $it")
                    throw IllegalStateException("测试一下")
                } catch (e: Exception) {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

class ServiceEvent(val message: String)