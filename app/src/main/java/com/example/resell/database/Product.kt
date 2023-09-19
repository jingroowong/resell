package com.example.resell.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productID: Int = 0,
    var productName: String,
    var productPrice: Double,
    var productDesc: String,
    var productCondition: String,
    var productImage: String,
    var dateUpload: Long,
    var productAvailability: Boolean
)