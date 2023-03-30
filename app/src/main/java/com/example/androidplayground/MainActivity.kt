package com.example.androidplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.*
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.androidplayground.ui.theme.AndroidPlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }

        (this as LifecycleOwner).lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Timber.d("event: $event")
                Timber.d("target state: ${event.targetState}")
            }
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("Thread在repeatOnLifecycle: ${Thread.currentThread()}, id: ${Thread.currentThread().id}")
                Timber.d("repeatOnLifecycle_started")
            }
        }
        Timber.d("Thread外层: ${Thread.currentThread()}, id: ${Thread.currentThread().id}")
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            Timber.d("Dispatchers: ${coroutineContext[CoroutineDispatcher]}")
            Timber.d("Thread里层1: ${Thread.currentThread()}, id: ${Thread.currentThread().id}")
            try {
                val result = withStarted { //
                    // NOTE 妈的，为什么这段代码要固定在Main线程上啊，这源码
                    Timber.d("Thread里层2: ${Thread.currentThread()}, id: ${Thread.currentThread().id}")

                    repeat(10) {
                        Thread.sleep(3000)
                        Timber.d("delay 结束 $it")
                    }
                    "JeckOnly"
                }
                Timber.d("收到withStarted的result: $result")
            } catch (e: CancellationException) {
                Timber.d("withStarted被取消")
            }
        }

    }
}
