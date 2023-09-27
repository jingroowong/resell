package com.example.resell.admin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.example.resell.R
import com.example.resell.database.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

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

        var productCondition = ""
        val productConditionGroup =
            view.findViewById<RadioGroup>(R.id.conditionRadioGroup)
        val insertBtn = view.findViewById<Button>(R.id.insertBtn)
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
        insertBtn.setOnClickListener {
            val productName = view.findViewById<EditText>(R.id.nameEdit).text.toString().trim()
            val productPrice =
                view.findViewById<EditText>(R.id.priceEdit).text.toString().toDouble()
            val productDesc =
                view.findViewById<EditText>(R.id.descriptionEdit).text.toString().trim()

            val productImage = view.findViewById<EditText>(R.id.imageEdit).text.toString().trim()
            val date = Date().time





            if (productCondition.isNotEmpty()) {
                val product = Product(
                    productName = productName,
                    productPrice = productPrice,
                    productDesc = productDesc,
                    productCondition = productCondition,
                    productImage = productImage,
                    dateUpload = date,
                    productAvailability = true
                )
                writeNewProduct(product)
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
                } else {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
            }
        })
//        db.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var count = dataSnapshot.childrenCount
//                count += 1
//                product.productID = count.toInt()
//                db.child(count.toString()).setValue(product)
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle errors here if necessary
//
//            }
//        })

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