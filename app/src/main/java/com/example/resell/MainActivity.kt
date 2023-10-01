package com.example.resell


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.resell.admin.AdminViewProduct
import com.example.resell.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the bottom navigation view here
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.history -> findNavController(R.id.myNavHostFragment).navigate(R.id.action_adminViewProduct_to_orderHistory)
                R.id.home -> findNavController(R.id.myNavHostFragment)
                // Add other navigation items as needed
                else -> {
                }
            }
            true
        }

        // Set up navigation controller for fragments
        val navController = findNavController(R.id.myNavHostFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}
