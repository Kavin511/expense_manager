package com.devstudio.designSystem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform