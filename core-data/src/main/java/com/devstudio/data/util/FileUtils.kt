package com.devstudio.data.util

import android.content.Context
import android.os.Build
import android.os.Environment

object FileUtils {
    fun getBackupFolder(context: Context) = if (Build.VERSION.SDK_INT >= 33) {
        context.getExternalFilesDir(null)?.absolutePath + "/Backups"
    } else {
        buildString {
            append(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS,
                ).absolutePath
            )
            append("/${context.applicationInfo.packageName}")
        }
    }
}