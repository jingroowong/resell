package com.example.resell.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.util.Date

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = false)
    var productID: Int = 0,
    var productName: String? = null,
    var productPrice: Double? = null,
    var productDesc: String? = null,
    var productCondition: String? = null,
    var productImage: String? = null,
    var dateUpload: Long? = null,
    var productAvailability: Boolean? = null
){

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productID" to productID,
            "productName" to productName,
            "productPrice" to productPrice,
            "productDesc" to productDesc,
            "productCondition" to productCondition,
            "productImage" to productImage,
            "dateUpload" to dateUpload,
            "productAvailability" to productAvailability
        )
    }
}