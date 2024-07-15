package com.devstudio.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
class Category(
    @PrimaryKey() @ColumnInfo(name = "id") var id: String = randomUUID(),
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "status") var status: Boolean = true,
    @ColumnInfo(name = "timeStamp") var timeStamp: Long = 0,
    @ColumnInfo(name = "categoryType") var categoryType: String,
    @ColumnInfo(name = "bookId", defaultValue = "0") var bookId: Long = 0,
)
expect fun randomUUID(): String