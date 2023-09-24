package com.example.resell


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.resell.admin.AdminViewProduct
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.google.firebase.FirebaseApp
import java.util.Date


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        val navController = this.findNavController(R.id.myNavHostFragment)



        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = AdminViewProduct() // Replace with your fragment class

        fragmentTransaction.replace(R.id.test, fragment)
        fragmentTransaction.commit()




    }

}