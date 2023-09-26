package com.example.resell.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_table", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userID"],
        childColumns = ["userID"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Payment::class,
        parentColumns = ["paymentID"],
        childColumns = ["paymentID"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class Order(
    var orderID : Int ?= null,
    var orderDate: String ?= null,
    var orderAmount : Double ?= null,
    var orderStatus : String ?= null,
    var userID: Int ?= null,
    var paymentID: Int ?= null
)
