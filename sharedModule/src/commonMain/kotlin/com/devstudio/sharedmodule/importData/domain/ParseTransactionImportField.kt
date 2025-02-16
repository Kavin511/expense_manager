package com.devstudio.sharedmodule.importData.domain

import com.devstudio.sharedmodule.importData.model.MetaInformation
import com.devstudio.sharedmodule.parseDateToTimestamp
import com.devstudio.utils.utils.TransactionMode
import com.devstudio.utils.utils.TransactionMode.EXPENSE
import com.devstudio.utils.utils.TransactionMode.INCOME
import com.devstudio.utils.utils.TransactionMode.INVESTMENT

fun parseAmount(value: String?): Double? {
    return value?.filter { it.isDigit() || it == '.' || it == '-' }?.toDoubleOrNull()
}

fun parseTransactionType(
    value: String, metaInfo: MutableList<MetaInformation>? = null
): TransactionMode? {
    return tryParse {
        val cleanedValue = value.trim()
        if (metaInfo != null) {
            val metaInformation = metaInfo.firstOrNull {
                it.value.isNotEmpty() && it.value.equals(value, ignoreCase = true)
            }
            val transactionMode = metaInformation?.type as? TransactionMode
            if (transactionMode != null) {
                return@tryParse transactionMode
            }
        }
        when {
            cleanedValue.contains("income", ignoreCase = true) -> INCOME
            cleanedValue.contains("expense", ignoreCase = true) -> EXPENSE
            cleanedValue.contains("transfer", ignoreCase = true) -> INVESTMENT
            cleanedValue.contains("investment", ignoreCase = true) -> INVESTMENT
            cleanedValue.toDoubleOrNull()?.let { it > 0 } == true -> INCOME
            cleanedValue.toDoubleOrNull()?.let { it < 0 } == true -> EXPENSE
            else -> null
        }
    }
}

fun getDateFormats() = listOf(
    "dd-MM-yyyy",
    "dd/MM/yyyy",
    "dd.MM.yyyy",
    "dd MM yyyy",
    "dd-MM-yy",
    "MMMM dd, yyyy",
    "dd/MM/yy",
    "dd.MM.yy",
    "d.MM.yy",
    "d-M-yy",
    "dd,MM,yyyy",
    "d/M/yy",
    "d.M.yy",
    "d.M.yy.",
    "d-M-yyyy",
    "d/M/yyyy",
    "d.M.yyyy",
    "d. M. yyyy.",
    "d. M. yyyy",
    "d MMM, yyyy",
    "dd MMM, yyyy",
    "d. MMMM yyyy",
    "dd.MM.yyyy.",
    "dd.MM.yy.",
    "yyyy-MM-dd",
    "yyyy/MM/dd",
    "yyyy.MM.dd",
    "yyyy MM dd",
    "yy/MM/dd",
    "yy-MM-dd",
    "yy.M.d",
    "yy-M-d",
    "yy. M. d",
    "yyyy/M/d",
    "yyyy年MM月dd日",
    "yy年M月d日",
    "yyyy.MM.dd.",
    "yyyy. MM. dd",
    "MM-dd-yyyy",
    "MM/dd/yyyy",
    "MM.dd.yyyy",
    "MM dd yyyy",
    "MM-dd-yy",
    "MM/dd/yy",
    "M/dd/yy",
    "MMM-dd-yyyy",
    "MMM dd, yyyy",
    "MMM d, yyyy",
    "MMMM d, yyyy",
    "yyyy.dd.MM",
    "yy.d.M",
    "dd/MM/yyyy HH:mm:ss",
    "dd-MM-yyyy HH:mm:ss",
    "dd/MM/yyyy H:mm",
    "dd-MM-yyyy H:mm",
    "dd/MM/yyyy HH:mm",
    "dd-MM-yyyy HH:mm",
    "MMMM d yyyy",
    "d MMM yyyy HH:mm:ss",
    "d MMM yyyy H:mm",
    "d MMM yyyy HH:mm",
    "d MMM yyyy",
    "yyyy/dd/MM HH:mm:ss",
    "yyyy-dd-MM HH:mm:ss",
    "yyyy/dd/MM H:mm",
    "yyyy-dd-MM H:mm",
    "yyyy/dd/MM HH:mm",
    "yyyy-dd-MM HH:mm",
    "yyyy/dd/MM",
    "yyyy-dd-MM",
    "yyyy d MMM HH:mm:ss",
    "yyyy d MMM H:mm",
    "yyyy d MMM HH:mm",
    "yyyy d MMM",
    "yyyy dd MMM",
    "MM/dd/yyyy HH:mm:ss",
    "MM-dd-yyyy HH:mm:ss",
    "MM/dd/yyyy H:mm",
    "MM-dd-yyyy H:mm",
    "MM/dd/yyyy HH:mm",
    "MM-dd-yyyy HH:mm",
    "MMM d yyyy HH:mm:ss",
    "MMMM d, yyyy",
    "MMM d yyyy H:mm",
    "MMM d yyyy HH:mm",
    "MMM dd yyyy",
    "yyyy/MM/dd HH:mm:ss",
    "yyyy-MM-dd HH:mm:ss",
    "yyyy/MM/dd H:mm",
    "yyyy-MM-dd H:mm",
    "yyyy/MM/dd HH:mm",
    "yyyy-MM-dd HH:mm",
    "yyyy MMM d HH:mm:ss",
    "yyyy MMM d H:mm",
    "yyyy MMM d HH:mm",
    "yyyy MMM dd"
)

fun parseDate(
    value: String?
): Long? = tryParse {
    value ?: return@tryParse null
    val cleanedValue = value.filter {
        it.isLetterOrDigit() || it == '-' || it == '/' || it == ':' || it == ' '
    }
    val possibleFormats = getDateFormats()
    for (format in possibleFormats) {
        val timestamp = parseDateToTimestamp(cleanedValue, format)
        if (timestamp != null) {
            return@tryParse timestamp
        }
    }
    null
}


fun notBlankTrimmedString(value: String?): String? =
    value?.trim().takeIf { it?.isNotBlank() == true }

private fun <T> tryParse(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}
