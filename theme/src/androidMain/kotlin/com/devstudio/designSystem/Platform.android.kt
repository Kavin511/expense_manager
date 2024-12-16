package com.devstudio.designSystem

import java.util.Calendar

class AndroidPlatform : Platform {
    override fun getCurrentTimeMillis(): Long {
        return Calendar.getInstance().timeInMillis
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()