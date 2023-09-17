package com.example.resell.models

import java.util.Date


data class Item(
    val id: Long?,
    val name: String,
    val price: Double,
    val image: String,
    val dateUpload: Date,
    val status: String
) {
    constructor(
        name: String,
        price: Double,
        image: String,
        dateUpload: Date,
        status: String
    ) : this(null, name, price, image, dateUpload, status)
}
