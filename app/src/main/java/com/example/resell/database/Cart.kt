package com.example.resell.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.database.PropertyName

@Entity(
    tableName = "carts_table",
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
data class Cart (

    var orderID: Int=0,
    var productID: Int=0,
    var productName:String?=null,
    var productImage: String?=null,
    var productPrice:Double?=null

)