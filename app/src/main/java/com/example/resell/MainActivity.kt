package com.example.resell

import ProductFragment
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.resell.database.AppDatabase
import com.google.firebase.FirebaseApp


public class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        val navController = this.findNavController(R.id.myNavHostFragment)
    }

}