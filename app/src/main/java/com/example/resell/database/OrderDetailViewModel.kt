package com.example.resell.database
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailViewModel(
    val database: OrderDetailDao,
    application: Application
) : ViewModel() {

    fun insertOrderDetail(orderDetail: OrderDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(orderDetail)
        }
    }


    fun getOrderDetailById(orderId: Int,productId: Int): OrderDetail? {
        return database.get(orderId,productId)
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }

        }

    }
}
