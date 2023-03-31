package com.example.androidplayground.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

object MoshiJsonUtil {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    inline fun <reified T> anyToJson(o: T): String {
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
        // val jsonAdapter: JsonAdapter<BlackjackHand> = moshi.adapter() 实验性
        val json: String = jsonAdapter.toJson(o)
        Timber.d(json)
        return json
    }
}

fun Any.toJson() =
    MoshiJsonUtil.anyToJson(this)