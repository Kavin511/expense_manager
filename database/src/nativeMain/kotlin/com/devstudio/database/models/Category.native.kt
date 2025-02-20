package com.devstudio.database.models

import platform.Foundation.NSUUID

actual fun randomUUID(): String {
    return NSUUID().UUIDString
}