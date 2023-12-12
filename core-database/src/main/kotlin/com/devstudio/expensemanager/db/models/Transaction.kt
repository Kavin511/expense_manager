package com.devstudio.expensemanager.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "transactions_table", foreignKeys = [
        ForeignKey(
            entity = Books::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bookId", defaultValue = "0") var bookId: Long = 0,
    @ColumnInfo(name = "note") var note: String = "",
    @ColumnInfo(name = "amount") var amount: Double = 0.0,
    @ColumnInfo(name = "categoryId") var categoryId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "isEditingOldTransaction") var transactionMode: String = "",
    @ColumnInfo(name = "transactionDate") var transactionDate: String = "",
    @ColumnInfo(name = "paymentStatus") var paymentStatus: String = "COMPLETED"
)