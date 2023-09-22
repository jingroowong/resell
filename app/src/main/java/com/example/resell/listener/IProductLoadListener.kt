package com.example.resell.listener

import com.example.resell.database.Product

interface IProductLoadListener {
    fun onProductLoadSuccess(productModelList:List<Product>?)
    fun onProductLoadFailed(message:String?)
}