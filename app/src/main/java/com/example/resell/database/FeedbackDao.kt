package com.example.resell.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface FeedbackDao {

    @Insert
    fun insert(feedback: Feedback)

    @Update
    fun update(feedback: Feedback)

    @Query("Delete from feedback_table Where feedbackID= :key")
    fun deleteById(key: Int): Int

    @Query("Select * from feedback_table Where feedbackID= :key")
    fun get(key: Int): Int

    @Query("Select * from feedback_table")
    fun getAll(): List<Feedback>?

    @Query("DELETE FROM feedback_table")
    fun clear()
}