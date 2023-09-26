package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    @Insert
    fun insert(order: Order)

    @Update
    fun update(order: Order)

    @Query("SELECT * FROM order_table WHERE orderID = :key")
    fun get(key: Int): Order?

    @Query("DELETE FROM order_table")
    fun clear()

    @Query("SELECT * FROM order_table ORDER BY orderID DESC LIMIT 1")
    fun getLatestOrder(): Order?

    @Query("SELECT * FROM order_table ORDER BY orderID DESC")
    fun getAllOrders(): LiveData<List<Order>>
}
