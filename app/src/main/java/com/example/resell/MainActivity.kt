package com.example.resell


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        val navController = this.findNavController(R.id.myNavHostFragment)



//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val fragment = AdminViewProduct() // Replace with your fragment class
//
//        fragmentTransaction.replace(R.id.test, fragment)
//        fragmentTransaction.commit()




    }

}