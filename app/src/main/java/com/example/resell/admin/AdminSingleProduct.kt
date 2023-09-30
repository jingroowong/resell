package com.example.resell.admin

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

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
        val pImage = view.findViewById<ImageView>(R.id.pImage)
        val pAvailability=view.findViewById<TextView>(R.id.availabilityTextView)
        val deleteBtn = view.findViewById<Button>(R.id.deleteBtn)
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)
        val uploadBtn = view.findViewById<Button>(R.id.pUploadBtn)

        var productCondition = ""
        var imageUri: Uri? = null


        val productID = arguments?.getInt(ARG_PRODUCT_ID, -1)

        var imagePickerActivityResult: ActivityResultLauncher<Intent> =

            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null) {
                    // getting URI of selected Image
                    imageUri = result.data?.data
                    pImage.setImageURI(imageUri)

                }
            }


        if (productID != -1) {
            if (productID != null) {
                viewModel.getProductById(productID).observe(viewLifecycleOwner) { product ->
                    if (product != null) {

                        pName.setText(product.productName)
                        pPrice.setText(product.productPrice.toString())
                        pDesc.setText(product.productDesc)
                        productCondition = product.productCondition.toString()

                        if (product.productAvailability == false) {
                            pAvailability.text = "Sold"
                            pAvailability.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                        } else {
                            pAvailability.text = "On Sale"
                            pAvailability.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
                        }

                        if (productCondition == "Moderate") {
                            pRadioGroup.check(R.id.moderateRB)
                        } else {
                            pRadioGroup.check(R.id.goodRB)
                        }

                        val storage = Firebase.storage.reference
                        val gsReference = storage.child(product.productImage.toString())

                        gsReference.downloadUrl.addOnSuccessListener { uri ->
                            // Load image into ImageView using Picasso
                            Picasso.get()
                                .load(uri)
                                .into(pImage)
                        }.addOnFailureListener { exception ->
                            // Handle any errors that occurred during the download
                        }

//                        Picasso.get()
//                            .load(product.productImage) // Replace with your product's image URL field
//                            .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder while loading
//                            .error(R.drawable.ic_launcher_background) // Optional error image to display if loading fails
//                            .into(pImage)


                        pRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                            when (checkedId) {
                                R.id.moderateRB -> {
                                    productCondition = "Moderate"
                                }

                                R.id.goodRB -> {
                                    productCondition = "Good"
                                }
                            }
                        }




                        uploadBtn.setOnClickListener {
                            val imagePickerIntent = Intent(Intent.ACTION_PICK)
                            imagePickerIntent.type = "image/*"
                            imagePickerActivityResult.launch(imagePickerIntent)
                        }

                        deleteBtn.setOnClickListener {
                            deleteProduct(product)
                        }

                        updateBtn.setOnClickListener {
                            val errorMessages = HashMap<String, String>()

                            val productNameText = pName.text.toString()
                            val productPriceText = pPrice.text.toString()
                            var productPrice = 0.0
                            val productDescText = pDesc.text.toString()

                            if (productNameText.isEmpty()) {
                                errorMessages["name"] = "Product name is required."
                            } else if (productNameText.length > 50) {
                                errorMessages["name"] =
                                    "Product name must not greater than 50 characters."
                            }



                            if (productPriceText.isEmpty()) {
                                errorMessages["price"] = "Product price is required."
                            } else {
                                try {
                                    productPrice = productPriceText.toDouble()
                                    if (productPrice <= 0) {
                                        errorMessages["price"] = "Price must be greater than 0"
                                    }
                                } catch (e: NumberFormatException) {
                                    errorMessages["price"] = "Invalid product price format."
                                }
                            }


                            if (productDescText.isEmpty()) {
                                errorMessages["desc"] = "Description is required."
                            } else if (productNameText.length > 200) {
                                errorMessages["desc"] =
                                    "Product description must not greater than 200 characters."
                            }

                            if (pRadioGroup.checkedRadioButtonId == -1) {
                                errorMessages["condition"] = "Please select a product condition."
                            }

                            product.productName = productNameText
                            product.productPrice = productPrice
                            product.productDesc = productDescText
                            product.productCondition = productCondition




                            editProduct(product, imageUri)
                        }

                    }
                }
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

            db.child(product.productID.toString()).removeValue()
                .addOnSuccessListener {
                    val storageRef = Firebase.storage.reference
                    val desertRef = storageRef.child(product.productImage.toString())

                    desertRef.delete().addOnSuccessListener {
                        showSuccessDialog(
                            "Product Deleted",
                            "The product has been successfully deleted."
                        )
                    }.addOnFailureListener {
                        Log.d("Delete", "Delete Image Failed")
                    }


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

    fun editProduct(product: Product, imageUri: Uri?) {

        val updateValue = product.toMap()
        val productIDToUpdate = product.productID
        val updateData = mapOf("$productIDToUpdate" to updateValue)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Product")
        builder.setMessage("Are you sure you want to update this product's detail?")
        builder.setPositiveButton("Update") { _, _ ->

            if (imageUri != null) {
                uploadImageToFirebaseStorage(product.productImage.toString(), imageUri)
            }

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

    private fun uploadImageToFirebaseStorage(path: String, selectedImageUri: Uri?) {

        if (selectedImageUri != null) {
            val storageRef = Firebase.storage.reference

            val imageRef = storageRef.child(path)

            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putFile(selectedImageUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    Log.d("Image", "URL=$imageUrl")
                }.addOnFailureListener { error ->
                    Log.e("Image", "Error getting download URL: ${error.message}")
                }
            }.addOnFailureListener { error ->
                Log.e("Image", "Error uploading image: ${error.message}")
            }

        }

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
            clearLocal()

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