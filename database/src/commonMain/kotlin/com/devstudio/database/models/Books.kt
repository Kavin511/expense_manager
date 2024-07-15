package com.devstudio.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books_table")
class Books(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", defaultValue = 0.toString()) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "timeStamp", defaultValue = "0") var timeStamp: Long = 0,
)
