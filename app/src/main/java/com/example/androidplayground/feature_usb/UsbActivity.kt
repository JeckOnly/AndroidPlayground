package com.example.androidplayground.feature_usb

import android.content.Context
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import timber.log.Timber


class UsbActivity : ComponentActivity() {

    private val usbReceiver: UsbDetachCheckReceiver = UsbDetachCheckReceiver()
    private val usbPermissionReceiver: UsbPermissionReceiver = UsbPermissionReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerUsbReceiver()
        registerUsbPermissionReceiver()


        setContent {
            Button(onClick = {
                listUsbDevice()
            }) {
                Text(text = "枚举设备")
            }
        }
    }

    private fun registerUsbPermissionReceiver() {
        registerReceiver(usbPermissionReceiver, IntentFilter().apply {
            addAction(UsbPermissionReceiver.ACTION)
        })
    }

    private fun registerUsbReceiver() {
        val usbDeviceStateFilter = IntentFilter()
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, usbDeviceStateFilter)
    }

    private fun listUsbDevice() {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList = manager.deviceList
        Timber.d("Size: ${deviceList.size}")
        deviceList.forEach {
            val usbDevice = it.value
            Timber.d("deviceId: ${usbDevice.deviceId}")
            Timber.d("vendorId: ${usbDevice.vendorId}")
            Timber.d("deviceName: ${usbDevice.deviceName}")
            Timber.d("productId: ${usbDevice.productId}")// persistent
            Timber.d("productName: ${usbDevice.productName}")
//            Timber.d("serialNumber: ${usbDevice.serialNumber}")// 需要权限
            Timber.d("version: ${usbDevice.version}")
            Timber.d("--------------------------------------------------")
        }
    }
}
