package com.example.resell.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "feedback_table",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userID"],
        childColumns = ["userID"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Feedback(
    @PrimaryKey(autoGenerate = true)
    var feedbackID: Int = 0,
    var feedbackDate: Long,
    var content: String,
    var reply: String,
    var userID: Int

)