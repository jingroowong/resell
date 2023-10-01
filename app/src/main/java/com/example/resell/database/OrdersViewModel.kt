package com.example.resell.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrdersViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialize the OrderDao from your database
    private val orderDao: OrderDao = AppDatabase.getInstance(application).orderDao

    // LiveData for the list of orders
    val allOrders: LiveData<List<Order>> = orderDao.getAllOrders()

    // Insert an order into the database using coroutines
    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            orderDao.insert(order)
        }
    }

    // Get a single order by its ID
    fun getOrderById(orderId: Int): LiveData<Order?> {
        return orderDao.get(orderId)
    }

    fun clearAllOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            orderDao.clear()
        }
    }

    // Function to fetch data from Firebase if needed



}

