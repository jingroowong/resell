package com.example.resell.listener

import com.example.resell.database.Order

interface IUpdateOrderStatusListener {
    fun onOrderLoadSuccess(orderModelList: MutableList<Order>)
    fun onOrderLoadFailed(message:String?)
}