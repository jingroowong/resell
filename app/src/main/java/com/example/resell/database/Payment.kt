package com.example.resell.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_table")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "paymentID")
    var paymentID: Int= 0,

    @ColumnInfo(name = "paymentDate")
    var paymentDate: Long,

    @ColumnInfo(name = "paymentAmount")
    var paymentAmount: Double,

    @ColumnInfo(name = "paymentType")
    var paymentType: String
)
