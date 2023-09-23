package com.example.resell.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.PropertyName
import java.util.Date

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var productID: Int = 0,
    @PropertyName("productName")
    var productName: String?=null,
    @PropertyName("productPrice")
    var productPrice: Double?=null,
    @PropertyName("productDesc")
    var productDesc: String?=null,
    @PropertyName("productCondition")
    var productCondition: String?=null,
    @PropertyName("productImage")
    var productImage: String?=null,
    @PropertyName("dateUpload")
    var dateUpload: String?=null,
    @PropertyName("productAvailability")
    var productAvailability: Boolean?=null,
    var key: String? = null // Add the key property
)