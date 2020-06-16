package io.appback.app_back.storage

import android.os.Build
import android.os.Environment
import android.os.StatFs

object StorageUtils {

    fun invokeAvailableStorage(callback: (result: String?) -> Unit) {
        val availableStorage = getAvailableStorage()
        if (availableStorage != null) callback(availableStorage)
        else callback(null)
    }

    fun invokeTotalStorage(callback: (result: String?) -> Unit) {
        val totalStorage = getTotalStorage()
        if (totalStorage != null) callback(totalStorage)
        else callback(null)
    }

    private fun getAvailableStorage(): String? {
        return if (hasExternalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                "${String.format("%.2f", (stat.blockSizeLong * stat.availableBlocksLong) / 1000000000.0).toDouble()} GB"
            } else {
                return null
            }
        } else null
    }

    private fun getTotalStorage(): String? {
        return if (hasExternalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                "${String.format("%.2f", (stat.blockCountLong * stat.blockSizeLong) / 1000000000.0).toDouble()} GB"
            } else {
                return null
            }
        } else null
    }

    private fun hasExternalMemoryAvailable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}