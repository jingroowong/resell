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
    fun get(orderID: Int, productID: Int): LiveData<OrderDetail?>

    @Query("DELETE FROM order_detail_table")
     fun clear()

    @Query("SELECT * FROM order_detail_table")
    fun getAllOrderDetails(): LiveData<List<OrderDetail>>

}
