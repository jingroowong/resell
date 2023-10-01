package com.example.resell.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel(
    val database: ProductDao,
    application: Application
) : ViewModel() {


    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(product)
        }
    }


    fun getProductById(productId: Int): LiveData<Product?> {
        return database.get(productId)
    }

    fun getAllProducts(): LiveData<List<Product>> {
        return database.getAll()
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }

        }

    }


    fun searchByName(productName:String):LiveData<List<Product>>{
        return database.searchByName(productName)
    }
}