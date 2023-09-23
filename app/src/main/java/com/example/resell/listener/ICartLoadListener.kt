package com.example.resell.listener

import com.example.resell.database.Cart

interface ICartLoadListener {
    fun onLoadCartSuccess(cartList:List<Cart>)
    fun onLoadCartFailed(message:String?)
}