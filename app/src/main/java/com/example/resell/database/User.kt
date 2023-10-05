package com.example.resell.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = false)
    var userID: String,
    var userName: String,
    var userEmail: String,
    var userDOB: Long,
    var userPhone: String,
    var userAddress: String,
    var password: String,
    var adminRole: Boolean
)