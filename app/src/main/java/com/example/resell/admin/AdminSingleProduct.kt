package com.example.resell.admin

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val db = Firebase.database.getReference("Products")

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


        val pName = view.findViewById<EditText>(R.id.pNameEdit)
        val pPrice = view.findViewById<EditText>(R.id.pPriceEdit)
        val pDesc = view.findViewById<EditText>(R.id.pDescEdit)
        val pRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val pImageUrl = view.findViewById<EditText>(R.id.pImageUrlEdit)
        val pImage = view.findViewById<ImageView>(R.id.pImage)
        val deleteBtn = view.findViewById<Button>(R.id.deleteBtn)
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)

        var productCondition=""

        val productID = arguments?.getInt(ARG_PRODUCT_ID, -1)

        if (productID != -1) {
            if (productID != null) {
                viewModel.getProductById(productID).observe(viewLifecycleOwner, { product ->
                    if (product != null) {

                        pName.setText(product.productName)
                        pPrice.setText(product.productPrice.toString())
                        pDesc.setText(product.productDesc)

                        if (product.productCondition == "Moderate") {
                            pRadioGroup.check(R.id.moderateRB)
                        } else {
                            pRadioGroup.check(R.id.goodRB)
                        }

                        pImageUrl.setText(product.productImage)



                        Picasso.get()
                            .load(product.productImage) // Replace with your product's image URL field
                            .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder while loading
                            .error(R.drawable.ic_launcher_background) // Optional error image to display if loading fails
                            .into(pImage)


                        pRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                            when (checkedId) {
                                R.id.moderateRB -> {
                                    productCondition = "Moderate"
                                }
                                R.id.goodRB -> {
                                    productCondition = "Good"
                                }
                            }
                        }

                        deleteBtn.setOnClickListener {
                            deleteProduct(product)
                        }

                        updateBtn.setOnClickListener {
                            product.productName = pName.text.toString()
                            product.productPrice = pPrice.text.toString().toDouble()
                            product.productDesc = pDesc.text.toString()
                            product.productCondition = productCondition
                            product.productImage = pImageUrl.text.toString()
                            editProduct(product)
                        }

                    }
                })
            }

        } else {
            // Handle the case where the argument was not passed correctly
        }


    }

    fun deleteProduct(product: Product) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Product")
        builder.setMessage("Are you sure you want to delete this product?")
        builder.setPositiveButton("Delete") { _, _ ->

            clearLocal()


            db.child(product.productID.toString()).removeValue()
                .addOnSuccessListener {
                    Log.d("P", "ProductDeleted")
                    showSuccessDialog(
                        "Product Deleted",
                        "The product has been successfully deleted."
                    )

                }
                .addOnFailureListener { error ->

                    Log.e(TAG, "Error deleting product: ${error.message}")
                }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            // User clicked the "Cancel" button, do nothing
        }
        val dialog = builder.create()
        dialog.show()

    }

    fun editProduct(product: Product) {

        val updateValue = product.toMap()
        val productIDToUpdate = product.productID
        val updateData = mapOf("$productIDToUpdate" to updateValue)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Product")
        builder.setMessage("Are you sure you want to update this product's detail?")
        builder.setPositiveButton("Update") { _, _ ->

            clearLocal()

            db.updateChildren(updateData)
                .addOnSuccessListener {
                    showSuccessDialog(
                        "Product Updated",
                        "The product has been successfully updated."
                    )
                }
                .addOnFailureListener { error ->
                    // An error occurred while updating the data
                    // Handle the error here
                    Log.e(TAG, "Error updating data: ${error.message}")
                }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            // User clicked the "Cancel" button, do nothing
        }
        val dialog = builder.create()
        dialog.show()

    }

    fun clearLocal() {
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).productDao
        val viewModelFactory = ProductViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        viewModel.clearAll()
    }

    fun showSuccessDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            findNavController().navigate(R.id.action_adminSingleProduct_to_adminViewProduct)
        }
        val dialog = builder.create()
        dialog.show()

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