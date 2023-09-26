package com.example.resell.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialize the OrderDetailDao from your database
    private val orderDetailDao: OrderDetailDao = AppDatabase.getInstance(application).orderDetailDao

    // Insert an order detail into the database using coroutines
    fun insertOrderDetail(orderDetail: OrderDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            orderDetailDao.insert(orderDetail)
        }
    }

    // Get a single order detail by order ID and product ID
    fun getOrderDetailById(orderId: Int, productId: Int): LiveData<OrderDetail?> {
        return orderDetailDao.get(orderId, productId)
    }

    // Clear all order details using coroutines
    fun clearAllOrderDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            orderDetailDao.clear()
        }
    }

    // LiveData to observe local product data
    val localOrderDetails: LiveData<List<OrderDetail>> = orderDetailDao.getAllOrderDetails()

}
