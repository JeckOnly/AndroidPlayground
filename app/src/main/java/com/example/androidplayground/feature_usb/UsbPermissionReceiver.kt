package com.example.androidplayground.feature_usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.widget.Toast


/**
 * Created by JeckOnly on 2023/6/9
 * Describe:
 */
class UsbPermissionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (ACTION == intent?.action) {

            synchronized(this) {
                val device: UsbDevice? = intent.getUsbDevice()

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    toast(context, "权限允许")
                    device?.apply {
                        //call method to set up device communication

                    }
                } else {
                    toast(context, "permission denied for device $device")
                }
            }
        }
    }

    companion object {
        const val ACTION = "com.android.example.USB_PERMISSION"
    }
}

fun Intent.getUsbDevice(): UsbDevice? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
    } else {
        this.getParcelableExtra(UsbManager.EXTRA_DEVICE)
    }
}
