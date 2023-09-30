package com.example.resell.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("Delete from user_table Where userID= :key")
    fun deleteById(key: Int): Int

    @Query("Select * from user_table Where userID= :key")
    fun get(key: Int): LiveData<User?>

    @Query("Select * from user_table")
    fun getAll(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    fun clear()
}