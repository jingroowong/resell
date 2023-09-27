package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PaymentDao {
    @Insert
    fun insert(payment: Payment)

    @Update
    fun update(payment: Payment)

    @Query("SELECT * FROM payment_table WHERE paymentID = :key")
    fun get(key: Int): Payment?

    @Query("DELETE FROM payment_table")
    fun clear()

    @Query("SELECT * FROM payment_table ORDER BY paymentID DESC LIMIT 1")
    fun getLatestPayment(): Payment?

    @Query("SELECT * FROM payment_table ORDER BY paymentID DESC")
    fun getAllPayments(): LiveData<List<Payment>>
}
