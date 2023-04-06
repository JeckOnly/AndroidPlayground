package com.example.androidplayground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidplayground.ui.theme.AndroidPlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppEventCenter.subscribeLatest<String>{
                    delay(3000)
                    Timber.d("收到: $it")
                }
            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppEventCenter.subscribe<Int>{
                    delay(10000)
                    Timber.d("收到: $it")
                }
            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppEventCenter.subscribe<ShowLoader>{
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


        setContent {
            AndroidPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Column {
                    Button(onClick = {
                            Timber.d("开始发送String")
                            AppEventCenter.postEvent("hello")
                            Timber.d("发送完毕String")
                    }) {
                        Text(text = "发送String值")
                    }
                    Button(onClick = {
                            Timber.d("开始发送Int")
                            AppEventCenter.postEvent(1)
                            Timber.d("发送完毕Int")
                    }) {
                        Text(text = "发送Int值")
                    }

                    Button(onClick = {
                            Timber.d("开始发送ShowLoader")
                            AppEventCenter.postEvent(ShowLoader("lalalalala"))
                            Timber.d("发送完毕ShowLoader")
                    }) {
                        Text(text = "发送ShowLoader值")
                    }

                    Button(onClick = {
                            Timber.d("开始发送ServiceEvent")
                            AppEventCenter.postEvent(ServiceEvent("lalalalala"))
                            Timber.d("发送完毕ServiceEvent")
                    }) {
                        Text(text = "发送ServiceEvent值")
                    }

                    Button(onClick = {
                           startService(Intent(this@MainActivity, TestShareFlowEventService::class.java))
                    }) {
                        Text(text = "start service to test")
                    }

                    Button(onClick = {
                           stopService(Intent(this@MainActivity, TestShareFlowEventService::class.java))
                    }) {
                        Text(text = "stop service to test")
                    }
                }
            }
        }
    }
}

class ShowLoader(val message: String)