package com.example.androidplayground.feature_usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import timber.log.Timber


/**
 * Created by JeckOnly on 2023/6/9
 * Describe:
 */
class UsbDetachCheckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val usbDevice = intent.getUsbDevice()
                toast(context ,"连接")
                Timber.d("接收到USB插入：$usbDevice")
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                toast(context ,"断开")
            }
        }
    }


}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

