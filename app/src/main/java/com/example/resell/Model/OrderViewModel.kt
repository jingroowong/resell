package com.example.resell.Model

import OrderRepository
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.database.Order
import com.example.resell.database.OrderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(
    val database: OrderDao,
    application: Application
) : ViewModel() {
    fun insertOrderStatus(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(order)
        }
    }

    fun getOrderByID(orderID: Int): LiveData<Order?> {
        return database.get(orderID)
    }

    fun getAllOrder(): LiveData<List<Order>> {
        return database.getAll()
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }

        }

    }
}