package com.devstudio.sharedmodule.domain.useCase.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: Kavin
 * @Date: 09/07/24
 */
fun String.contains(vararg values: String): Boolean {
    return values.any { this.contains(it, ignoreCase = true) }
}

fun parseDateToTimestamp(dateStr: String): Long? {
    val dateFormats = getDateFormats()
    var parsedDate: Date? = null
    for (format in dateFormats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            parsedDate = sdf.parse(dateStr)
            if (parsedDate != null) break
        } catch (_: ParseException) {}
    }
    return parsedDate?.time
}

private fun getDateFormats() = listOf(
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
    "yy.d.M"
)

fun getCategoryMapping() = mapOf(
    "Food" to listOf(
        "Food", "restaurant", "cafe", "food", "meal", "dinner", "lunch", "breakfast", "snack"
    ),
    "Rent" to listOf("Rent", "rent", "lease", "apartment", "housing"),
    "Transportation" to listOf(
        "Transportation", "uber", "lyft", "taxi", "bus", "train", "metro", "fuel", "gas", "parking"
    ),
    "Groceries" to listOf("Groceries", "supermarket", "grocery", "market", "store"),
    "Home and utilities" to listOf(
        "Home and utilities", "electricity", "water", "gas", "internet", "phone", "utility"
    ),
    "Insurance" to listOf("Insurance", "insurance", "policy"),
    "Bills & EMIs" to listOf("Bills & EMIs", "bill", "emi", "payment", "installment"),
    "Education" to listOf(
        "Education", "tuition", "school", "college", "university", "course", "book"
    ),
    "Health and personal care" to listOf(
        "Health and personal care", "doctor", "hospital", "pharmacy", "medicine", "clinic", "dental"
    ),
    "Entertainment" to listOf(
        "Entertainment", "movie", "theatre", "concert", "streaming", "subscription"
    ),
    "Sports" to listOf("Sports", "gym", "fitness", "sports", "equipment"),
    "Earned Income" to listOf("Earned Income", "salary", "wage", "paycheck", "employment"),
    "Profit Income" to listOf("Profit Income", "business profit", "profit", "earnings"),
    "Interest Income" to listOf("Interest Income", "interest", "savings interest", "fixed deposit"),
    "Dividend Income" to listOf("Dividend Income", "dividend", "stock dividend", "share dividend"),
    "Rental Income" to listOf("Rental Income", "rent received", "rental", "lease income"),
    "Capital Gains Income" to listOf(
        "Capital Gains Income", "capital gain", "investment profit", "stock sale"
    ),
    "Royalty Income" to listOf("Royalty Income", "royalty", "patent", "copyright")
)

