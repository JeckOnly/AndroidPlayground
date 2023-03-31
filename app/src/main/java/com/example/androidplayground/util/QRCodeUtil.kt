package com.example.androidplayground.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.androidplayground.R
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape

fun String.urlToQRCode(context: Context, @DrawableRes imageId: Int): Drawable {
    val data = QrData.Url(this)
    val options = createQrVectorOptions {
        padding = .125f
        logo {
            drawable = ContextCompat
                .getDrawable(context, imageId)
            size = .25f
            padding = QrVectorLogoPadding.Natural(.2f)
            shape = QrVectorLogoShape
                .Circle
        }
    }
    return QrCodeDrawable(data, options)
}