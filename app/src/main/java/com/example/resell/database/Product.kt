package com.example.resell.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productID: Int = 0,
    var key: String?=null,
    var productName: String?=null,
    var productPrice: Double?=null,
    var productDesc: String?=null,
    var productCondition: String?=null,
    var productImage: String?=null,
    var dateUpload: Date?=null,
    var productAvailability: Boolean?=null
)