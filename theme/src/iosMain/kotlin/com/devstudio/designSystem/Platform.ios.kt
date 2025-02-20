package com.devstudio.designSystem

import kotlinx.datetime.Clock
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override fun getCurrentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

}

actual fun getPlatform(): Platform = IOSPlatform()