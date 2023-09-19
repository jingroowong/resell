package com.example.resell


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import java.util.Date


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val date = Date().time.toLong()
        val product = Product(
            productName = "product3",
            productPrice = 1.00,
            productDesc = "New Product",
            productCondition = "Bad",
            productImage = "Image3.jpg",
            dateUpload = date,
            productAvailability = true
        )

        val dataSource = AppDatabase.getInstance(application).productDao
        val viewModelFactory = ProductViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

//        viewModel.insertProduct(product)

        viewModel.getAllProducts().observe(this, { products ->
            if (products != null) {

                val resultTextView = findViewById<TextView>(R.id.result2)
                resultTextView.text = products.toString()

            }})



//        viewModel.getProductById(1).observe(this, { product ->
//            if (product != null) {
//                val resultTextView = findViewById<TextView>(R.id.result2)
//                resultTextView.text = product.toString()
//            }
//        }
//        )


//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val fragment = AdminViewProduct() // Replace with your fragment class
//
//        fragmentTransaction.replace(R.id.test, fragment)
//        fragmentTransaction.commit()


    }

}