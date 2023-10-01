package com.example.resell.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Feedback::class, Order::class, OrderDetail::class, Payment::class, Product::class, User::class],
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract val feedbackDao: FeedbackDao
    abstract val orderDao:OrderDao
    abstract val orderDetailDao:OrderDetailDao
    abstract val paymentDao:PaymentDao
    abstract val productDao: ProductDao
    abstract val userDao:UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "resell_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}
