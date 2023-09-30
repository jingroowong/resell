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
    fun get(key: Int): LiveData<Order?>

    @Query("DELETE FROM order_table")
    fun clear()

    @Query("Select * from order_table")
    fun getAll(): LiveData<List<Order>>



}
