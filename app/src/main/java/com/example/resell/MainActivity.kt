package com.example.resell

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.resell.database.AppDatabase
import com.example.resell.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFrament(OrderHistory())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
//                R.id.home -> replaceFrament(Home())
//                R.id.cart -> replaceFrament(Cart())
                R.id.history -> replaceFrament(OrderHistory())
//                R.id.profile -> replaceFrament(Profile())

                else ->{

                }
            }
            true
        }
    }

    private fun replaceFrament(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

//    private  fun loadFragment(fragment: Fragment){
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragment_container,fragment)
//        transaction.commit()
//    }

}