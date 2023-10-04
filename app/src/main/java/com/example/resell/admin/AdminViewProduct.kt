package com.example.resell.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.MainActivity
import com.example.resell.R
import com.example.resell.database.Product
import com.example.resell.database.ProductViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

    private val db = Firebase.database.getReference("Products")
    private lateinit var viewModel: ProductViewModel


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
        val rootView = inflater.inflate(R.layout.fragment_admin_view_product, container, false)

//        val viewModel = (requireActivity() as MainActivity).productViewModel
//
//        viewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
//            if (products.isEmpty()) {
//                readFirebaseProduct(viewModel)
//            }
//        }

        return inflater.inflate(R.layout.fragment_admin_view_product, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager

        // Access the productViewModel from the MainActivity
       viewModel = (requireActivity() as MainActivity).productViewModel


        viewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                val adapter = ProductAdapter(products)
                recyclerView.adapter = adapter
            }else{
                readFirebaseProduct(viewModel)
                val adapter = ProductAdapter(products)
                recyclerView.adapter = adapter
            }
        }


        val addBtn = view.findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener {

            findNavController().navigate(R.id.action_adminViewProduct_to_adminInsertProduct)

        }

        val searchBtn = view.findViewById<Button>(R.id.searchBtn)

        searchBtn.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchEditText).text.toString().trim()
            viewModel.searchByName(searchText).observe(viewLifecycleOwner){
                    products->
                if(products.isNotEmpty()){
                    val adapter = ProductAdapter(products)
                    recyclerView.adapter = adapter
                }else{
                    Toast.makeText(requireContext(),"No product(s) found!",Toast.LENGTH_LONG).show()
                }
            }
        }


    }


    fun readFirebaseProduct(viewModel: ProductViewModel) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productSnapshot in dataSnapshot.children) {
                    // Convert the Firebase data to a Product object
                    val productData = productSnapshot.getValue(Product::class.java)

                    if (productData != null) {
                        viewModel.getProductById(productData.productID)
                            .observe(viewLifecycleOwner, { product ->
                                if (product == null) {
                                    viewModel.insertProduct(productData)
                                }

                            })


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