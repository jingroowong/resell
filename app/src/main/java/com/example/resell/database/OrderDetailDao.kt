package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDetailDao {
    @Insert
    suspend fun insert(orderDetail: OrderDetail)

    @Update
    suspend fun update(orderDetail: OrderDetail)

    @Query("SELECT * FROM order_detail_table WHERE orderID = :orderID AND productID = :productID")
    suspend fun get(orderID: Int, productID: Int): OrderDetail?

    @Query("DELETE FROM order_detail_table")
    suspend fun clear()

    @Query("SELECT * FROM order_detail_table WHERE orderID = :orderID")
    fun getOrderDetailsForOrder(orderID: Int): LiveData<List<OrderDetail>>

    @Query("SELECT * FROM order_detail_table WHERE productID = :productID")
    fun getOrderDetailsForProduct(productID: Int): LiveData<List<OrderDetail>>
}
