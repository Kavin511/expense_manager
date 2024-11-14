package com.devstudio.sharedmodule.domain.useCase

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */
fun readFile(context: Context, uri: Uri, charset: Charset): String? {
    var fileContent: String? = null
    context.contentResolver.openFileDescriptor(uri, "r")?.use {
        FileInputStream(it.fileDescriptor).use { fileInputStream ->
            fileContent = readFileContent(
                fileInputStream = fileInputStream,
                charset = charset
            )
        }
    }
    return fileContent
}

@Throws(IOException::class)
private fun readFileContent(
    fileInputStream: FileInputStream,
    charset: Charset
): String {
    BufferedReader(InputStreamReader(fileInputStream, charset)).use { br ->
        val sb = StringBuilder()
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
            sb.append('\n')
        }
        return sb.toString()
    }
}