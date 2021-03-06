package io.appback.app_back.battery

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

internal object BatteryUtils {

    fun invokeBatteryLevel(applicationContext: Context, callback: (result: String?) -> Unit) {
        val batteryLevel = getBatteryLevel(applicationContext)
        if (batteryLevel != -1) {
            callback(batteryLevel.toString())
        } else {
            callback(null)
        }
    }

    private fun getBatteryLevel(applicationContext: Context): Int? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager: BatteryManager = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (intent != null) intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            else null
        }
    }
}