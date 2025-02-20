package com.devstudio.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions_table",
    foreignKeys = [
        ForeignKey(
            entity = Books::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bookId", defaultValue = "0") var bookId: Long = 0,
    @ColumnInfo(name = "note") var note: String = "",
    @ColumnInfo(name = "amount") var amount: Double = 0.0,
    @ColumnInfo(name = "categoryId") var categoryId: String = randomUUID(),
    @ColumnInfo(name = "isEditingOldTransaction") var transactionMode: String = "",
    @ColumnInfo(name = "transactionDate") var transactionDate: String = "",
    @ColumnInfo(name = "paymentStatus") var paymentStatus: String = "COMPLETED",
    @ColumnInfo(name = "dataSource") var dataSource: Int = DataSource.MANUAL.ordinal
)

enum class DataSource(val value: Int) {
    MANUAL(0), CSV(1);

    companion object {
        fun fromValue(ordinal: Int): DataSource {
            return entries.toTypedArray().firstOrNull { it.value == ordinal } ?: MANUAL
        }
    }
}