package com.example.resell.admin

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminSingleProduct.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminSingleProduct : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).productDao
        val viewModelFactory = ProductViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)


        val pName=view.findViewById<TextView>(R.id.pName)
        val pPrice=view.findViewById<TextView>(R.id.pPrice)
        val pImage=view.findViewById<ImageView>(R.id.pImage)

        val productID = arguments?.getInt(ARG_PRODUCT_ID,-1)

        if (productID != -1) {
            if (productID != null) {
                viewModel.getProductById(productID).observe(viewLifecycleOwner, { product ->
                    if (product != null) {

                        pName.text=product.productName
                        pPrice.text=product.productPrice.toString()
                        Picasso.get()
                            .load(product.productImage) // Replace with your product's image URL field
                            .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder while loading
                            .error(R.drawable.ic_launcher_background) // Optional error image to display if loading fails
                            .into(pImage)
                    }
                })
            }

        } else {
            // Handle the case where the argument was not passed correctly
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_single_product, container, false)
    }

    companion object {
        const val ARG_PRODUCT_ID = "productID"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminSingleProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}