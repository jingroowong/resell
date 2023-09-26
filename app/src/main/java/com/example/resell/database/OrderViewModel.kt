package com.example.resell.database
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(
    val database: OrderDao,
    application: Application
) : ViewModel() {

    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(order)
        }
    }


    fun getOrderById(orderId: Int): Order? {
        return database.get(orderId)
    }

    fun getAllOrders(): LiveData<List<Order>> {
        return database.getAllOrders()
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }

        }

    }
}
