package com.devstudio.sharedmodule

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.devstudio.data.datastore.DataSourceModule
import com.devstudio.data.repository.TransactionsRepositoryImpl
import com.devstudio.data.repository.UserDataRepositoryImpl
import com.devstudio.database.AppContext
import com.devstudio.sharedmodule.domain.useCase.csvToTransaction.CsvToTransactionMapper
import com.devstudio.sharedmodule.domain.useCase.readFile
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionMapResult
import com.opencsv.CSVReaderBuilder
import com.opencsv.validators.LineValidator
import com.opencsv.validators.RowValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.StringReader
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8


@Composable
actual fun FilePicker(
    show: Boolean,
    onFileSelected: (List<CSVRow>?) -> Unit,
) {
    val context = LocalContext.current
    val launcher =
        simpleActivityForResultLauncher(intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }) { _, intent ->
            intent?.data?.also {
                onFileSelected(
                    processFile(
                        context, it, UTF_8
                    )
                )
            }
        }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(Unit)
        }
    }
}


fun processFile(context: Context, uri: Uri, charset: Charset): List<CSVRow>? {
    return try {
        val fileContent: String? = readFile(context, uri, charset = Charsets.UTF_8)
        var parsedCsvList = parseCSV(fileContent!!, normalizeCSV = false)
        if (parsedCsvList.size < 3) {
            parsedCsvList = parseCSV(fileContent, normalizeCSV = true)
        }
        parsedCsvList
    } catch (e: Exception) {
        if (charset != Charsets.UTF_16) {
            return processFile(context, uri, Charsets.UTF_16)
        }
        return null
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

actual suspend fun saveTransactions(transactions: List<List<String>>): TransactionMapResult {
    val context = AppContext.get()!!
    val transactionsRepositoryImpl = TransactionsRepositoryImpl(
        context, UserDataRepositoryImpl(DataSourceModule(context))
    )
    return CoroutineScope(Dispatchers.IO).async {
        val transactionMapResult = CsvToTransactionMapper(transactions).invoke(context)
        transactionMapResult.transactions.forEach {
            transactionsRepositoryImpl.upsertTransaction(it.transaction)
        }
        return@async transactionMapResult
    }.await()
}
