package com.example.resell.admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.resell.MainActivity
import com.example.resell.R
import com.example.resell.database.AppDatabase
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.example.resell.database.ProductViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminInsertProduct.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminInsertProduct : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val db = Firebase.database.getReference("Products")
    private var imageUrl = ""


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_insert_product, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = (requireActivity() as MainActivity).productViewModel


        var productCondition = ""
        val productConditionGroup =
            view.findViewById<RadioGroup>(R.id.conditionRadioGroup)
        val insertBtn = view.findViewById<Button>(R.id.insertBtn)
        val uploadBtn = view.findViewById<Button>(R.id.uploadImageBtn)
        val imagePreview = view.findViewById<ImageView>(R.id.imagePreview)


        var imageUri: Uri? = null



        productConditionGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.moderateRadioBtn -> {
                    productCondition = "Moderate"
                }

                R.id.goodRadioBtn -> {
                    productCondition = "Good"
                }

            }
        }
        var imagePickerActivityResult: ActivityResultLauncher<Intent> =

            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null) {
                    // getting URI of selected Image
                    imageUri = result.data?.data
                    imagePreview.setImageURI(imageUri)

                }
            }


        uploadBtn.setOnClickListener {
            val imagePickerIntent = Intent(Intent.ACTION_PICK)
            imagePickerIntent.type = "image/*"
            imagePickerActivityResult.launch(imagePickerIntent)

        }

        insertBtn.setOnClickListener {


            val productNameText = view.findViewById<EditText>(R.id.nameEdit).text.toString().trim()
            var productPriceText =
                view.findViewById<EditText>(R.id.priceEdit).text.toString().trim()
            val productDescText =
                view.findViewById<EditText>(R.id.descriptionEdit).text.toString().trim()

            val date = Date().time
            var productPrice: Double = 0.0


                //Input Validation
            val errorMessages = HashMap<String, String>()


            if (productNameText.isEmpty()) {
                errorMessages["name"] = "Product name is required."
            } else if (productNameText.length > 50) {
                errorMessages["name"] = "Product name must not greater than 50 characters."
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
                errorMessages["desc"] = "Product description must not greater than 200 characters."
            }

            if (productConditionGroup.checkedRadioButtonId == -1) {
                errorMessages["condition"] = "Please select a product condition."
            }

            if (imageUri == null) {
                errorMessages["image"] = "Please select an image."
            }


            //Insert product if not errors
            if (errorMessages.isEmpty()) {
                uploadImageToFirebaseStorage(imageUri)

                if (productCondition.isNotEmpty()) {
                    val product = Product(
                        productName = productNameText,
                        productPrice = productPrice,
                        productDesc = productDescText,
                        productCondition = productCondition,
                        productImage = imageUrl,
                        dateUpload = date,
                        productAvailability = true
                    )
                    viewModel.clearAll()
                    writeNewProduct(product)

                }
            } else {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Validation Errors")

                val message = StringBuilder()
                for ((field, errorMessage) in errorMessages) {
                    message.append("$errorMessage\n")
                }

                dialogBuilder.setMessage(message.toString())

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = dialogBuilder.create()
                dialog.show()
            }


        }

    }


    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri?) {
        Log.d("Product", "ImageURI2:${selectedImageUri}")

        if (selectedImageUri != null) {
            val storageRef = Firebase.storage.reference
            imageUrl =
                "images/${System.currentTimeMillis()}" // Unique file name based on timestamp

            // Create a reference to the image file in Firebase Storage
            val imageRef = storageRef.child(imageUrl)

            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putFile(selectedImageUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    imageUrl = downloadUri.toString()
                    Log.d("Image", "URL=$imageUrl")
                }.addOnFailureListener { error ->
                    Log.e("Image", "Error getting download URL: ${error.message}")
                }
            }.addOnFailureListener { error ->
                Log.e("Image", "Error uploading image: ${error.message}")
            }

        }

    }


    fun writeNewProduct(product: Product) {
        val query = db.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val lastProductID = dataSnapshot.children.first().key
                    if (lastProductID != null) {


                        var lastProductIDInt = lastProductID.toInt()
                        lastProductIDInt += 1
                        product.productID = lastProductIDInt

                        db.child(lastProductIDInt.toString()).setValue(product)
                            .addOnSuccessListener {
                                findNavController().navigate(R.id.action_adminInsertProduct_to_adminViewProduct)
                            }.addOnFailureListener { error ->
                                Log.e(ContentValues.TAG, "Error deleting product: ${error.message}")
                            }


                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminInsertProduct.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminInsertProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}