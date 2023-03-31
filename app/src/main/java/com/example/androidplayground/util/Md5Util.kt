package com.example.androidplayground.util

import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Md5Util {
        fun encrypt(input: String): String {
            return try {
                val md = MessageDigest.getInstance("MD5")
                val messageDigest = md.digest(input.toByteArray())
                val hexString = StringBuilder()
                for (b in messageDigest) {
                    hexString.append(String.format("%02x", b))
                }
                hexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
}


fun String.toMd5Str(): String {
    return Md5Util.encrypt(this)
}

