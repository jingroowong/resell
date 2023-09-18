package com.example.resell.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface FeedbackDao {

    @Insert
    suspend fun insert(feedback: Feedback)

    @Update
    suspend fun update(feedback: Feedback)

    @Query("Delete from feedback_table Where feedbackID= :key")
    suspend fun deleteById(key: Int): Feedback?

    @Query("Select * from feedback_table Where feedbackID= :key")
    suspend fun get(key: Int): Feedback?

    @Query("Select * from feedback_table")
    suspend fun getAll(): List<Feedback>?

    @Query("DELETE FROM feedback_table")
    suspend  fun clear()
}