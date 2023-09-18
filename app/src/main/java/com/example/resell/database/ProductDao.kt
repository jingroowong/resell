package com.example.resell.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface ProductDao {

    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Query("Delete from product_table Where productId= :key")
    suspend fun deleteById(key: Int): Product?

    @Query("Select * from product_table Where productID= :key")
    suspend fun get(key: Int): Product?

    @Query("Select * from product_table")
    suspend fun getAll(): List<Product>?

    @Query("DELETE FROM product_table")
    suspend  fun clear()
}