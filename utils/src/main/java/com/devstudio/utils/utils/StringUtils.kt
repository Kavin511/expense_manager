package com.devstudio.utils.utils

fun String.toPascalCase(): String {
    return if (isEmpty()) {
        ""
    } else substring(0, 1) + substring(1, length)
}
