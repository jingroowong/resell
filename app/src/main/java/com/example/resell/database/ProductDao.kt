package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert
    fun insert(product: Product)

    @Update
    fun update(product: Product)

    @Query("Delete from product_table Where productId= :key")
    fun deleteById(key: Int): Int

    @Query("Select * from product_table Where productID= :key")
    fun get(key: Int): LiveData<Product?>

    @Query("Select * from product_table")
    fun getAll(): LiveData<List<Product>>

    @Query("DELETE FROM product_table")
    fun clear()
}