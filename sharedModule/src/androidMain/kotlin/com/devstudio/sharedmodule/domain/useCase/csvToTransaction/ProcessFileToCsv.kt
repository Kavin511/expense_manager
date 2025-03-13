package com.devstudio.sharedmodule.domain.useCase.csvToTransaction

import android.content.Context
import android.net.Uri
import com.devstudio.sharedmodule.domain.useCase.readFile
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.opencsv.CSVReaderBuilder
import com.opencsv.validators.LineValidator
import com.opencsv.validators.RowValidator
import java.io.StringReader
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

/**
 * @Author: Kavin
 * @Date: 01/01/25
 */
class ProcessFileToCsv {
    fun invoke(context: Context, uri: Uri, charset: Charset): List<CSVRow>? {
        return try {
            val fileContent: String? = readFile(context, uri, charset = UTF_8)
            var parsedCsvList = parseCSV(fileContent!!, normalizeCSV = false)
            if (parsedCsvList.size < 3) {
                parsedCsvList = parseCSV(fileContent, normalizeCSV = true)
            }
            parsedCsvList
        } catch (e: Exception) {
            if (charset != Charsets.UTF_16) {
                invoke(context, uri, Charsets.UTF_16)
            }
            null
        }
    }

    private fun parseCSV(csv: String, normalizeCSV: Boolean): List<CSVRow> {
        val finalCSV = if (normalizeCSV) {
            csv.replace(",", " ")
                .replace(";", ",")
        } else {
            csv
        }
        val csvReader = CSVReaderBuilder(StringReader(finalCSV))
            .withLineValidator(object : LineValidator {
                override fun isValid(line: String?): Boolean {
                    return true
                }

                override fun validate(line: String?) {
                }
            })
            .withRowValidator(object : RowValidator {
                override fun isValid(row: Array<out String>?): Boolean {
                    return true
                }

                override fun validate(row: Array<out String>?) {
                }
            })
            .build()

        return csvReader.readAll()
            .map { CSVRow(it.toList()) }
    }
}