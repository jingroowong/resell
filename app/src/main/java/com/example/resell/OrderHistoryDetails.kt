package com.example.resell

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.adapter.MyOrderDetailsAdapter
import com.example.resell.database.Cart
import com.example.resell.database.Order
import com.example.resell.database.OrderDetails
import com.example.resell.database.Product
import com.example.resell.databinding.FragmentOrderHistoryDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryDetails.newInstance] factory method to
 * create an instance of this fragment.
 */

//private lateinit var viewModel: OrderDetailsModel

private lateinit var binding: FragmentOrderHistoryDetailsBinding

class OrderHistoryDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var orderDetailModels: MutableList<Cart> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentOrderHistoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val ARG_ORDER = "order"
        const val ARG_PRODUCT = "product"

        @JvmStatic
        fun newInstance(order: Order) = OrderHistory().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ORDER, order)
            }
        }

        @JvmStatic
        fun newInstance(product: Product) =
            OrderHistoryDetails().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the order data from arguments
        val order = arguments?.getParcelable(ARG_ORDER) as Order?
        val product = arguments?.getParcelable(ARG_PRODUCT) as Product?
        val backBtn = view.findViewById<ImageView>(R.id.btnBack)

        // Update UI with order details
        order?.let {
            val orderStatus = view.findViewById<TextView>(R.id.orderStatus)
            val orderDate = view.findViewById<TextView>(R.id.orderTime)
            val orderID = view.findViewById<TextView>(R.id.orderID)
            val orderAmount = view.findViewById<TextView>(R.id.orderPrice)

            orderStatus.text = it.orderStatus
            orderDate.text = it.orderDate
            orderID.text = it.orderID.toString()
            orderAmount.text = getString(R.string.price_format, it.orderAmount)
            Log.d("FirebaseData", "Show Order")

            backBtn.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
        // Fetch and display product data based on order details
        val orderID = order?.orderID ?: 0
        fetchOrderDetailsAndProductData(orderID)
    }

    private fun fetchOrderDetailsAndProductData(orderID: Int) {
        // Fetch order details for the specified orderID
        val orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
        val orderDetailQuery = orderDetailsRef.orderByChild("orderID").equalTo(orderID.toDouble())

        orderDetailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                val products = mutableListOf<Product>() // List to store products

                // Loop through order details
                for (orderDetailData in orderDetailsSnapshot.children) {
                    val orderDetail = orderDetailData.getValue(OrderDetails::class.java)

                    if (orderDetail != null) {
                        val productID = orderDetail.productID.toString()
                        Log.d(
                            "FirebaseData",
                            "fetchOrderDetailsAndProductData Found orderDetail: ${orderDetail.orderID}, ${orderDetail.productID}"
                        )

                        // Fetch product data for this order detail
                        fetchProductData(productID, orderDetail, products)
                    }
                }
                // After fetching all products, update the RecyclerView
                val recyclerView = view?.findViewById<RecyclerView>(R.id.productRecyclerView)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = MyOrderDetailsAdapter(products)
            }

            override fun onCancelled(orderDetailsError: DatabaseError) {
                // Handle order details data retrieval error
            }
        })
    }

    private fun fetchProductData(productID: String, orderDetail: OrderDetails, products: MutableList<Product>) {
        // Assuming you have a Firebase database reference
        val productRef = FirebaseDatabase.getInstance().getReference("Products").child(productID)

        // Query the database to fetch product data by productID
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(productSnapshot: DataSnapshot) {
                val product = productSnapshot.getValue(Product::class.java)

                // Update UI with the fetched product data
                if (product != null) {
                    products.add(product) // Add product to the list
                    // Notify the adapter that the data has changed
                    val recyclerView = view?.findViewById<RecyclerView>(R.id.productRecyclerView)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database query cancellation or errors
                Log.d("FirebaseData", "Database query error: ${databaseError.message}")
            }
        })
    }
}
