package com.example.androidplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidplayground.ui.theme.AndroidPlaygroundTheme
import com.example.androidplayground.util.ControlledRunner
import com.example.androidplayground.util.SingleRunner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val controlledRunner = ControlledRunner<String>()

    private val singleRunner = SingleRunner()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Button(onClick = {
                            scope.launch {
                               controlledRunner.cancelPreviousThenRun {
                                    val i = Random.nextInt()
                                    Timber.d("开始执行: $i")
                                    delay(5000)
                                    Timber.d("返回结果: $i")
                                    "hello form the other side"
                                }
                            }
                        }) {
                            Text(text = "提交任务-取消之前")
                        }

                        Button(onClick = {
                            scope.launch {
                                controlledRunner.joinPreviousOrRun {
                                    val i = Random.nextInt()
                                    Timber.d("开始执行: $i")
                                    delay(5000)
                                    Timber.d("返回结果: $i")
                                    "hello form the other side"
                                }
                            }
                        }) {
                            Text(text = "提交任务-使用之前")
                        }

                        Button(onClick = {
                            scope.launch {
                                singleRunner.afterPrevious {
                                    val i = Random.nextInt()
                                    Timber.d("开始执行: $i")
                                    delay(5000)
                                    Timber.d("返回结果: $i")
                                    "hello form the other side"
                                }
                            }
                        }) {
                            Text(text = "提交任务-顺序执行")
                        }
                    }
                }
            }
        }
    }
}
