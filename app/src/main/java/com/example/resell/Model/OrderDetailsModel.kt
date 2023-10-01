package com.example.resell.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resell.Repository.OrderDetailsRepository
import com.example.resell.database.OrderDetails

class OrderDetailsModel : ViewModel() {
    private val repository

    = OrderDetailsRepository().getInstance()
    private val _allOrdersDetail = MutableLiveData<List<OrderDetails>>()
    val allOrdersDetail: LiveData<List<OrderDetails>> = _allOrdersDetail

    init {
        repository.loadOrderDetail(_allOrdersDetail)
    }
}