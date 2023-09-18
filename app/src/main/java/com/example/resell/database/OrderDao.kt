package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: Order)

    @Update
    suspend fun update(order: Order)

    @Query("SELECT * FROM order_table WHERE orderID = :key")
    suspend fun get(key: Int): Order?

    @Query("DELETE FROM order_table")
    fun clear()

    @Query("SELECT * FROM order_table ORDER BY orderID DESC LIMIT 1")
    suspend fun getLatestOrder(): Order?

    @Query("SELECT * FROM order_table ORDER BY orderID DESC")
    suspend fun getAllOrders(): LiveData<List<Order>>
}
