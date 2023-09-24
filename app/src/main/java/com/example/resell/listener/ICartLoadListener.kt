package com.example.resell.listener

import com.example.resell.database.Cart
import java.io.Serializable

interface ICartLoadListener : Serializable {
    fun onLoadCartSuccess(cartList:List<Cart>)
    fun onLoadCartFailed(message:String?)
}