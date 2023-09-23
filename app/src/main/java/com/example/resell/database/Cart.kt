package com.example.resell.database

import com.google.firebase.database.PropertyName

class Cart {
    var key:String?=null
    @PropertyName("productName")
    var productName:String?=null
    @PropertyName("productImage")
    var productImage: String?=null
    @PropertyName("productPrice")
    var productPrice:Double?=null
}