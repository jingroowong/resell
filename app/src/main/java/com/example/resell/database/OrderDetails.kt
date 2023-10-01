package com.example.resell.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "order_details_table",
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
data class OrderDetails(
    var orderID: Int = 0,
    var productID: Int = 0
)
//data class OrderDetail(
//
//    @ColumnInfo(name = "orderID")
//    var orderID: Int,
//
//    @ColumnInfo(name = "productID")
//    var productID: Int,
//
//    @ColumnInfo(name = "subtotal")
//    var subtotal: Double
//)

