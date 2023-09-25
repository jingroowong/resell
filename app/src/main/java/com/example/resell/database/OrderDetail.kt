package com.example.resell.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "order_detail_table",
    primaryKeys = ["orderID", "productID"],
    foreignKeys = [ForeignKey(
        entity = Order::class,
        parentColumns = ["orderID"],
        childColumns = ["orderID"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Product::class,
        parentColumns = ["productID"],
        childColumns = ["productID"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class OrderDetail(

    @ColumnInfo(name = "orderID")
    var orderID: Int,

    @ColumnInfo(name = "productID")
    var productID: Int,

    @ColumnInfo(name = "subtotal")
    var subtotal: Double
)

data class OrderDetails(
    var orderID : Int ?= null,
    var orderDateTime: Long ?= null,
    var paymentDateTime : Double ?= null,
    var orderStatus : String ?= null,
    var completedDateTime: Int ?= null,
    var orderAmount: Int ?= null
)
