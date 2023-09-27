package com.example.resell.listener

import com.example.resell.database.Order


interface IOrderHistoryListener {
    fun onOrderLoadSuccess(orderModelList: MutableList<Order>)
    fun onOrderLoadFailed(message:String?)
}