package com.example.expensemanager.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions_table")
class Transactions(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "transactionMode") val transactionMode: String,
    @ColumnInfo(name = "transactionDate") val transactionDate: String
)