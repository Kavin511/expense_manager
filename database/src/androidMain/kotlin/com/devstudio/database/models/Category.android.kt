package com.devstudio.database.models

import android.content.Context
import java.lang.ref.WeakReference
import java.util.UUID

actual fun randomUUID(): String {
    return UUID.randomUUID().toString()
}
