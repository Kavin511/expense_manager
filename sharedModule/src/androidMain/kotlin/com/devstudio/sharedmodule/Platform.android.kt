package com.devstudio.sharedmodule

import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.devstudio.data.datastore.DataSourceModule
import com.devstudio.data.repository.TransactionsRepositoryImpl
import com.devstudio.data.repository.UserDataRepositoryImpl
import com.devstudio.database.AppContext
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.domain.useCase.csvToTransaction.ProcessFileToCsv
import com.devstudio.sharedmodule.importData.model.CSVRow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
            val data = intent?.data
            if (data != null) {
                val csvRows = ProcessFileToCsv().invoke(context, data, UTF_8)
                onFileSelected(csvRows)
            } else {
                onFileSelected(null)
            }
        }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(Unit)
        }
    }
}


actual suspend fun saveTransactions(transactions: List<Transaction>): Result<Int> {
    val context = AppContext.get()!!
    val userDataRepository = UserDataRepositoryImpl(DataSourceModule(context))
    val transactionsRepositoryImpl =
        TransactionsRepositoryImpl(userDataRepository)
    val importedCount = transactionsRepositoryImpl.insertTransactions(transactions)
    return if (transactions.size == importedCount) {
        Result.success(importedCount)
    } else {
        Result.failure(Throwable())
    }
}


@Composable
actual fun isPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == ORIENTATION_LANDSCAPE) {
        configuration.screenWidthDp < 840
    } else {
        configuration.screenWidthDp < 600
    }
}

actual fun parseDateToTimestamp(dateStr: String, format: String): Long? {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val parsedDate: Date?
    try {
        parsedDate = sdf.parse(dateStr)
        return parsedDate?.time
    } catch (_: ParseException) {
        return null
    }
}

@Composable
actual fun showToastAlert(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}