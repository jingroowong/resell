package com.example.resell.listener

import com.example.resell.database.Order

interface IOrderHistoryListener {
    fun onOrderLoadSuccess(orderModelList:List<Order>?)
    fun onOrderLoadFailed(message:String?)
}