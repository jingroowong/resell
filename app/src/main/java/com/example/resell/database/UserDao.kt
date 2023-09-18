package com.example.resell.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("Delete from user_table Where userID= :key")
    suspend fun deleteById(key: Int): User?

    @Query("Select * from user_table Where userID= :key")
    suspend fun get(key: Int): User?

    @Query("Select * from user_table")
    suspend fun getAll(): List<User>?

    @Query("DELETE FROM user_table")
    suspend  fun clear()
}