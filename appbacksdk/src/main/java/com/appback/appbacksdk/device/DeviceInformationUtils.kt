package com.appback.appbacksdk.device

import android.content.Context
import com.appback.appbacksdk.AppbackConstants.EMPTY_STRING
import com.appback.appbacksdk.AppbackConstants.EVENT_APP_VERSION
import com.appback.appbacksdk.AppbackConstants.EVENT_BATTERY_LEVEL
import com.appback.appbacksdk.AppbackConstants.EVENT_CARRIER_NAME
import com.appback.appbacksdk.AppbackConstants.EVENT_CONNECTION_TYPE
import com.appback.appbacksdk.AppbackConstants.EVENT_DEVICE_ID
import com.appback.appbacksdk.AppbackConstants.EVENT_DEVICE_NAME
import com.appback.appbacksdk.AppbackConstants.EVENT_DEVICE_ORIENTATION
import com.appback.appbacksdk.AppbackConstants.EVENT_DEVICE_STORAGE
import com.appback.appbacksdk.AppbackConstants.EVENT_SYSTEM_VERSION
import io.appback.app_back.battery.BatteryUtils
import io.appback.app_back.connectivity.ConnectivityUtils
import io.appback.app_back.device.DeviceUtils
import io.appback.app_back.storage.StorageUtils

class DeviceInformationUtils {

    companion object{
        fun  getDeviceInformation(context: Context): HashMap<String, String> {

            var deviceInfoHash = HashMap<String, String>()

            BatteryUtils.invokeBatteryLevel(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_BATTERY_LEVEL] = "$it %"
                }
            })

            DeviceUtils.invokeDeviceID(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_DEVICE_ID] = it.toUpperCase()
                }
            })

            DeviceUtils.invokeDeviceVersion {
                if (it != null) {
                    deviceInfoHash[EVENT_SYSTEM_VERSION] = it
                }
            }

            DeviceUtils.invokeApplicationVersion(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_APP_VERSION] = it
                }
            })

            DeviceUtils.invokeDeviceName {
                if (it != null) {
                    deviceInfoHash[EVENT_DEVICE_NAME] = it
                }
            }

            DeviceUtils.invokeDeviceOrientation(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_DEVICE_ORIENTATION] = it
                }
            })

            ConnectivityUtils.invokeCarrierName(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_CARRIER_NAME] = it
                }
            })

            ConnectivityUtils.invokeConnectionType(applicationContext = context, callback = {
                if (it != null) {
                    deviceInfoHash[EVENT_CONNECTION_TYPE] = it
                }
            })

            var totalStorage = EMPTY_STRING
            var freeStorage = EMPTY_STRING

            StorageUtils.invokeTotalStorage {
                if (it != null) {
                    totalStorage = it
                }
            }

            StorageUtils.invokeAvailableStorage {
                if (it != null) {
                    freeStorage = it
                }
            }

            deviceInfoHash[EVENT_DEVICE_STORAGE] = "$freeStorage / $totalStorage"

            return deviceInfoHash

        }
    }

}