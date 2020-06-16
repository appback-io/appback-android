package io.appback.app_back.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings

internal object DeviceUtils {

    fun invokeApplicationVersion(applicationContext: Context, callback: (result: String?) -> Unit) {
        val applicationVersion = getApplicationVersion(applicationContext)
        if (applicationVersion.isNullOrBlank()) callback(null)
        else callback(applicationVersion)
    }

    fun invokeDeviceID(applicationContext: Context, callback: (result: String?) -> Unit) {
        val deviceID = getDeviceID(applicationContext)
        if (deviceID.isEmpty()) callback(null)
        else callback(deviceID)
    }

    fun invokeDeviceName(callback: (result: String?) -> Unit) {
        val deviceName = getDeviceName()
        if (deviceName.isEmpty()) callback(null)
        else callback(deviceName)
    }

    fun invokeDeviceOrientation(applicationContext: Context, callback: (result: String?) -> Unit) {
        val deviceOrientation = getDeviceOrientation(applicationContext)
        if (deviceOrientation.isEmpty()) callback(null)
        else callback(deviceOrientation)
    }

    fun invokeDeviceVersion(callback: (result: String?) -> Unit) {
        val deviceVersion = getOSVersion()
        if (deviceVersion.isEmpty()) callback(null)
        else callback(deviceVersion)
    }

    private fun getApplicationVersion(applicationContext: Context): String? {
        return try {
            val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
            packageInfo.versionName
        } catch (exception: PackageManager.NameNotFoundException) {
            null
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceID(applicationContext: Context): String = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

    private fun getDeviceName(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    private fun getDeviceOrientation(context: Context): String {
        return when(context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> "Portrait"
            Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
            else -> "Undefined"
        }
    }

    private fun getOSVersion(): String = Build.VERSION.RELEASE
}