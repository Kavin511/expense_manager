package com.devstudio.theme

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform