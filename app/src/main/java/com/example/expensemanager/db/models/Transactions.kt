package com.example.expensemanager.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions_table")
class Transactions(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "note") var note: String,
    @ColumnInfo(name = "amount") var amount: Double,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "isEditingOldTransaction") var transactionMode: String,
    @ColumnInfo(name = "transactionDate") var transactionDate: String
)