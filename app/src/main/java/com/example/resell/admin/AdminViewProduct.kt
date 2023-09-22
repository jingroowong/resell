package com.example.resell.admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp


import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminViewProduct.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminViewProduct : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val date = Date().time.toLong()
//        val product = Product(
//            productName = "product3",
//            productPrice = 1.00,
//            productDesc = "New Product",
//            productCondition = "Bad",
//            productImage = "Image3.jpg",
//            dateUpload = date,
//            productAvailability = true
//        )
//
        val rootView = inflater.inflate(R.layout.fragment_admin_view_product, container, false)
//        val application = requireNotNull(this.activity).application
//        val dataSource = AppDatabase.getInstance(application).productDao
//        val viewModelFactory = ProductViewModelFactory(dataSource, application)
//        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)
//
//        viewModel.insertProduct(product)

//        viewModel.clearAll()
//        viewModel.getAllProducts().observe(this, { products ->
//            if (products != null) {
//
//                val resultTextView = rootView.findViewById<TextView>(R.id.result)
//                resultTextView.text = products.toString()
//
//            }})


//        viewModel.getProductById(1).observe(this, { product ->
//            if (product != null) {
//                val resultTextView = findViewById<TextView>(R.id.result2)
//                resultTextView.text = product.toString()
//            }
//        }
//        )
        return inflater.inflate(R.layout.fragment_admin_view_product, container, false)
    }

    override fun onResume() {
        super.onResume()
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).productDao
        val viewModelFactory = ProductViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        //https://firebase.google.com/docs/firestore/quickstart#kotlin+ktx_2
        val product = hashMapOf(
            "productID" to "unique_product_id",
            "productName" to "Product Name",
            "productPrice" to 19.99,
            "productDesc" to "Product description goes here",
            "productCondition" to "New",
            "productImage" to "product_image.jpg",
            "dateUpload" to Timestamp.valueOf("2022-01-01 11:11:11"),
            "productAvailability" to true
        )

        db.collection("product")
            .add(product)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        
        viewModel.getAllProducts().observe(this, { products ->
            if (products != null) {

                val resultTextView = requireView().findViewById<TextView>(R.id.result)
                resultTextView.text = products.toString()

            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Initialize the button by finding it in the fragment's layout
//        myButton = view.findViewById(R.id.my_button)
//
//        // Set an onClickListener for the button
//        myButton.setOnClickListener {
//            // Handle the button click event here
//            // You can perform actions or navigate to another fragment/activity
//        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminViewProduct.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminViewProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}