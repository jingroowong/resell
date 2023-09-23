package com.example.resell.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebaservrealdbk.Repository.OrderRepository
import com.example.resell.database.Order

class OrderViewModel : ViewModel() {

    private val repository : OrderRepository
    private val _allOrders = MutableLiveData<List<Order>>()
    val allOrders : LiveData<List<Order>> = _allOrders


    init {

        repository = OrderRepository().getInstance()
        repository.loadOrder(_allOrders)

    }

}