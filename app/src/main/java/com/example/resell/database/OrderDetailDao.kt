package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDetailDao {
    @Insert
    fun insert(orderDetail: OrderDetail)

    @Update
    fun update(orderDetail: OrderDetail)

    @Query("SELECT * FROM order_detail_table WHERE orderID = :orderID AND productID = :productID")
    fun get(orderID: Int, productID: Int): OrderDetail?

    @Query("DELETE FROM order_detail_table")
     fun clear()

    @Query("SELECT * FROM order_detail_table WHERE orderID = :orderID")
    fun getOrderDetailsForOrder(orderID: Int): LiveData<List<OrderDetail>>

    @Query("SELECT * FROM order_detail_table WHERE productID = :productID")
    fun getOrderDetailsForProduct(productID: Int): LiveData<List<OrderDetail>>
}
