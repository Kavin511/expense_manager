package com.devstudio.expensemanager.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "status") var status: Boolean = true,
    @ColumnInfo(name = "timeStamp") var timeStamp: Long = 0,
    @ColumnInfo(name = "categoryType") var categoryType: String
)