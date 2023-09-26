package com.example.resell.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Boolean.TRUE

class ProductViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialize the OrderDao from your database
    private val productDao: ProductDao = AppDatabase.getInstance(application).productDao

    // LiveData for the list of orders
    val localProducts: LiveData<List<Product>> = productDao.getAll()

    fun insertProduct(product: MutableList<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.insert(product)
        }
    }


    fun getProductById(productId: Int): LiveData<Product?> {
        return productDao.get(productId)
    }

    fun getAllProducts(): LiveData<List<Product>> {
        return productDao.getAll()
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                productDao.clear()
            }

        }
    }

    fun fetchDataFromFirebase() {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productModels: MutableList<Product> = ArrayList()
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(Product::class.java)
                            if (productModel != null && productModel.productAvailability == TRUE) {
                                productModels.add(productModel)
                            }
                        }
                        // Update the local database with the fetched data
                        productDao.insert(productModels)
                    } else {
                        // Handle the case where no data is found on Firebase
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
    }
}