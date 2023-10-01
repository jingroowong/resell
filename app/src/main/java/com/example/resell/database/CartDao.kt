package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cart: Cart)

    @Delete
    fun delete(cart: Cart)

    @Query("SELECT * FROM carts_table WHERE orderID = :orderID AND productID = :productID")
    fun get(orderID: Int, productID: Int): LiveData<Cart?>

    @Query("DELETE FROM carts_table")
    fun clear()
}