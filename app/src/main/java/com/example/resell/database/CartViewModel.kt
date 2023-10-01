package com.example.resell.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialize the OrderDetailDao from your database
    private val cartDao: CartDao = AppDatabase.getInstance(application).cartDao

    // Insert an order detail into the database using coroutines
    fun insertCart(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
            cartDao.insert(cart)
        }
    }

    // Get a single order detail by order ID and product ID
    fun getCartById(orderId: Int, productId: Int): LiveData<Cart?> {
        return cartDao.get(orderId, productId)
    }

    // Clear all order details using coroutines
    fun clearAllCarts() {
        viewModelScope.launch(Dispatchers.IO) {
            cartDao.clear()
        }
    }


}
