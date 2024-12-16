package com.devstudio.designSystem

interface Platform {
    fun getCurrentTimeMillis(): Long
}

expect fun getPlatform(): Platform