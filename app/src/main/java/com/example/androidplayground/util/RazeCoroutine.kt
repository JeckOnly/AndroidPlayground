package com.example.androidplayground.util
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by JeckOnly on 2023/4/17
 * Describe: 协程工具类。用于并行执行多个协程，只要有一个成功返回，其他的协程会被取消。
 *
 * Note: select它也可以实现类似的功能。但select的处理能力有限，主要因为错误处理。
 */

/**
 * @param blockList 的参数不应使用return@raze 进行返回。 出于适用性考量不使用 crossinline
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun <A> razeN(
    ctx: CoroutineContext = EmptyCoroutineContext,
    vararg blockList: suspend CoroutineScope.() -> A,
): A = supervisorScope {
    val oneHasSuccess = AtomicBoolean(false)
    val result = AtomicReference<A>(null)

    val deferredList = blockList.map { block ->
        async(ctx) {
            block()
        }
    }

    val deferredCompletionHandler = { throwable: Throwable?, deferred: Deferred<A> ->
        if (throwable == null) {
            // 正常完成
            if (oneHasSuccess.compareAndSet(false, true) and result.compareAndSet(
                    null,
                    deferred.getCompleted()
                )
            ) {
                // 完成且成功赋值, 只会有一个成功完成的进入
                deferredList.forEach {
                    it.cancel()
                }
            } else {
                // 已经有其他任务正常完成且成功赋值
                // do nothing
            }
        } else {
            // 不正常完成，1）被取消 2）报错
            // do nothing
        }
    }


    // 本来相对整个List<Deferred>应用await all（只要有其中一个元素报错、被取消，抛出相应Exception（including CancellationException））
    // 会导致其他的block还未执行完就被取消（supervisorScope作用域本身抛出异常取消所有child）
    deferredList.onEach { deferred: Deferred<A> ->
        deferred.invokeOnCompletion { throwable: Throwable? ->
            deferredCompletionHandler(throwable, deferred)
        }
    }.map {
        try {
            it.await()
        } catch (e: Exception) {
            println(e.message)
        }
    }


    return@supervisorScope result.get()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun <A> raze2(
    ctx: CoroutineContext = EmptyCoroutineContext,
    crossinline f1: suspend CoroutineScope.() -> A,
    crossinline f2: suspend CoroutineScope.() -> A,
): A = supervisorScope {
    val oneHasSuccess = AtomicBoolean(false)
    val result = AtomicReference<A>(null)

    val deferredCompletionHandler = { throwable: Throwable?, myDeferred: Deferred<A>, otherDeferred: Deferred<A> ->
        if (throwable == null) {
            // 正常完成
            if (oneHasSuccess.compareAndSet(false, true) and result.compareAndSet(
                    null,
                    myDeferred.getCompleted()
                )
            ) {
                // 完成且成功赋值, 只会有一个成功完成的进入
                otherDeferred.cancel()
            } else {
                // 已经有其他任务正常完成且成功赋值
                // do nothing
            }
        } else {
            // 不正常完成，1）被取消 2）报错
            // do nothing
        }
    }

    val deferred1 = async(ctx) {
        f1()
    }
    val deferred2 = async(ctx) {
        f2()
    }
    deferred1.invokeOnCompletion {
        deferredCompletionHandler(it, deferred1, deferred2)
    }
    deferred2.invokeOnCompletion {
        deferredCompletionHandler(it, deferred2, deferred1)
    }

    try {
        deferred1.await()
    } catch (e: Exception) {
        println(e.message)
    }

    try {
        deferred2.await()
    } catch (e: Exception) {
        println(e.message)
    }

    return@supervisorScope result.get()
}

private fun test() {
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        val result = raze2(
            ctx = Dispatchers.Default,
            {
                delay(5500)
                "Jeck"
            },
            {
                delay(5000)
                throw IllegalStateException("报错")
                "hello"
            },
        )
        println(result)
    }
//    scope.launch {
//        val result = razeMany(
//            ctx = Dispatchers.Default,
//            {
//                delay(5500)
//                "Jeck"
//            },
//            {
//                delay(5000)
//                null
//            },
//            {
//                delay(4000)
//                throw IllegalStateException("报错")
//                "Tom"
//            }
//        )
//        println(result)
//    }
    Thread.sleep(1000000)
}