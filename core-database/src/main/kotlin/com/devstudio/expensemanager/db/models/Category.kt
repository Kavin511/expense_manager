package com.devstudio.expensemanager.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob
import java.util.UUID

@Entity(tableName = "category_table")
class Category(
    @PrimaryKey() @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "status") var status: Boolean = true,
    @ColumnInfo(name = "timeStamp") var timeStamp: Long = 0,
    @ColumnInfo(name = "categoryType") var categoryType: String,
    @ColumnInfo(name = "bookId") var bookId: Long = 0,
)