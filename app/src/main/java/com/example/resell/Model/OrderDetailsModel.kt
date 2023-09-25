package com.example.resell.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebaservrealdbk.Repository.OrderRepository
import com.example.resell.Repository.OrderDetailsRepository
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail

class OrderDetailsModel : ViewModel() {
    private val repository: OrderDetailsRepository
    private val _allOrdersDetail = MutableLiveData<List<OrderDetail>>()
    val allOrdersDetail: LiveData<List<OrderDetail>> = _allOrdersDetail

    init {
        repository = OrderDetailsRepository().getInstance()
        repository.loadOrderDetail(_allOrdersDetail)
    }
}