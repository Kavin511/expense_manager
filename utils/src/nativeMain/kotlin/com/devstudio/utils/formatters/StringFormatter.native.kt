package com.devstudio.utils.formatters

actual fun String.format(vararg args: Any?): String {
    var formattedString = this
    args.forEach { arg ->
        formattedString = when (arg) {
            is Float, is Double -> formattedString.replaceFirst("%f", arg.toString())
            else -> formattedString.replaceFirst("%s", arg.toString())
        }
    }
    return formattedString
}