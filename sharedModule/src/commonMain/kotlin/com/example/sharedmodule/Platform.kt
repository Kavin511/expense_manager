package com.example.sharedmodule

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform