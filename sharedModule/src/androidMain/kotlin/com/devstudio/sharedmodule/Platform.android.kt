package com.devstudio.sharedmodule

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.devstudio.data.datastore.DataSourceModule
import com.devstudio.data.repository.TransactionsRepositoryImpl
import com.devstudio.data.repository.UserDataRepositoryImpl
import com.devstudio.sharedmodule.domain.useCase.csvToTransaction.CsvToTransactionMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference


@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: Array<String>,
    title: String?,
    onFileSelected: (List<List<String>>?) -> Unit
) {
    val context = LocalContext.current
    var fileSelected by remember {
        mutableStateOf(false)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data?.data != null) {
                    onFileSelected(parseCsvFromUri(
                        context, result.data?.data!!
                    ))
                } else {
                    fileSelected = false
                }
            }
        }

    LaunchedEffect(show) {
        if (show) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("text/comma-separated-values");
            Intent.createChooser(intent, "Open CSV")
            launcher.launch(intent)
        }
    }
}

fun parseCsvFromUri(context: Context, uri: Uri): List<List<String>> {
    val contentResolver = context.contentResolver
    context.contentResolver.query(
        uri, arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE
        ), null, null, null
    )

    val inputStream = contentResolver.openInputStream(uri)
    return convert(inputStream)
}

@Throws(IOException::class)
fun convert(file: InputStream?): MutableList<List<String>> {
    file?.let {
        BufferedReader(InputStreamReader(it)).use { reader ->
            val lines = reader.readLines()
            val parsedCsv: MutableList<List<String>> = ArrayList()

            val regex = """"(.*?)"|([^,]+)""".toRegex()

            lines.forEach { line ->
                val matches = regex.findAll(line)
                val parsedLine = matches.map { matchResult ->
                    matchResult.groups[1]?.value ?: matchResult.groups[2]?.value ?: ""
                }.toList()
                parsedCsv.add(parsedLine)
            }

            return parsedCsv
        }
    } ?: return mutableListOf()
}
//    val wbs = WorkbookSettings();
//    wbs.gcDisabled = true;
//    val workbook = Workbook.getWorkbook(inputStream, wbs)
//    val posFile = POIFSFileSystem(file, true)
//    if (file.name.endsWith("xlsx")) {
//        val info = EncryptionInfo(posFile)
//        val d = Decryptor.getInstance((info))
//        if (!d.verifyPassword(password)) {
//            excelExceptionListData.postValue("Wrong password! ")
//            return@launch
//        }
//        workbook = XSSFWorkbook(d.getDataStream(posFile))
//    } else {
//        org.apache.poi.hssf.record.crypto.Biff8EncryptionKey.setCurrentUserPassword(
//            password
//        )
//        workbook = HSSFWorkbook(posFile.root, true)
//    }
//    val csvLines = mutableListOf<List<String>>()
//    for (i in 0 until workbook.getSheet(0).rows) {
//        val row = workbook.getSheet(0).getRow(i)
//        val subList = mutableListOf<String>()
//        for (element in row) {
//            subList.add(element.contents)
//            csvLines.add(subList)
//        }
//    }

//    try {
//        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//        val workbook = WorkbookFactory.create(inputStream)
//        val sheet = workbook.getSheetAt(0)
//
//        for (row in sheet) {
//            val subList = mutableListOf<String>()
//            for (cell in row) {
//                subList.add(cell.toString())
//            }
//            csvLines.add(subList)
//        }
//        inputStream?.close()
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
actual fun saveTransactions(transactions: List<List<String>>) {
    val context = AppContext.get()!!
    val transactionsRepositoryImpl = TransactionsRepositoryImpl(
        context, UserDataRepositoryImpl(DataSourceModule(context))
    )
    CoroutineScope(Dispatchers.IO).launch {
        val transactionList = CsvToTransactionMapper(transactions).invoke(context)
        transactionList.forEach {
            transactionsRepositoryImpl.upsertTransaction(it)
        }
    }
}


actual object AppContext {

    private var value: WeakReference<Context?>? = null

    fun set(context: Context) {
        value = WeakReference(context)
    }

    internal fun get(): Context? {
        return value?.get()
    }

}