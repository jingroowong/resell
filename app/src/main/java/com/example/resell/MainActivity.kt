package com.example.resell


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.resell.admin.AdminViewProduct
import com.example.resell.database.AppDatabase
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.example.resell.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val application = requireNotNull(this).application
        val dataSource = AppDatabase.getInstance(application).productDao
        val viewModelFactory = ProductViewModelFactory(dataSource, application)
        productViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        // Set up navigation controller for fragments
        navController = findNavController(R.id.myNavHostFragment)
    }

}
