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
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "orderID")
    var orderID: Int = 0,

    @ColumnInfo(name = "orderDate")
    var orderDate: String,

    @ColumnInfo(name = "orderAmount")
    var orderAmount: Double,

    @ColumnInfo(name = "orderStatus")
    var orderStatus: String,

    @ColumnInfo(name = "orderDeal")
    var deal: Boolean,

    @ColumnInfo(name = "userID")
    var userID: Int,

    @ColumnInfo(name = "paymentID")
    var paymentID: Int
) {
    // Add a no-argument constructor required by Firebase
    constructor() : this(0, "", 0.0, "", false, 0, 0)
}
