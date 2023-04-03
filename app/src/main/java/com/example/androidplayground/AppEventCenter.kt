package com.example.androidplayground

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * APP事件总线
 *
 * NOTE 生产者不阻塞了，但消费者对于给到自己的event是顺序执行，即如果收到两个事件给自己处理，那么先处理完一个再开始另一个
 */
object AppEventCenter {

    val shareFlow: MutableSharedFlow<Any> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun postEvent(any: Any) {
        shareFlow.tryEmit(any)
    }

    /**
     * 会挂起当前协程，所以应该在一个[launch]中调用该函数
     *
     * 需要自己在[observeCallback]中处理异常，否则会导致订阅流结束（本想用[catch]内部处理，但无法解决流结束的问题）
     */
    suspend inline fun <reified T : Any> subscribe(crossinline observeCallback: suspend (T) -> Unit) {
        shareFlow.filter {
            it is T
        }.onEach {
            observeCallback(it as T)
        }.collect()
    }
}