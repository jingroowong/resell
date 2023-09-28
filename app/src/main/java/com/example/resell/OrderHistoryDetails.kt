package com.example.resell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.resell.Model.OrderDetailsModel
import com.example.resell.adapter.MyOrderDetailsAdapter
import com.example.resell.database.Cart
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail
import com.example.resell.database.Product
import com.example.resell.databinding.FragmentOrderHistoryDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryDetails.newInstance] factory method to
 * create an instance of this fragment.
 */

private lateinit var viewModel: OrderDetailsModel
lateinit var orderDetailsAdapter: MyOrderDetailsAdapter
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
        private const val ARG_ORDER = "order"
        private const val ARG_PRODUCT = "product"

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

        if (product != null) {
            fetchProductData(product)
        }

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
        }

        loadOrderHistoryFromFireBase()

    }

    private fun fetchProductData(product: Product) {
        // Assuming you have a Firebase database reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("Products")

        // Query the database to fetch product data by productID
        databaseReference.child(product.productID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data for the product with the specified productID found
                    val productData = dataSnapshot.getValue(Product::class.java)

                    // Update your UI with the fetched product data
                    if (productData != null) {
                        // Update your UI elements with the product data
                        val productNameTextView = view!!.findViewById<TextView>(R.id.productName)
                        val productPriceTextView = view!!.findViewById<TextView>(R.id.productPrice)

                        productNameTextView.text = productData.productName
                        productPriceTextView.text = String.format("RM %.2f", productData.productPrice)
                    } else {
                        // Handle case where product data is null
                    }
                } else {
                    // Handle case where product with the specified productID doesn't exist
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database query cancellation or errors
            }
        })
    }


    private fun loadOrderHistoryFromFireBase() {
        val currentOrderID = FirebaseDatabase.getInstance().getReference("Order")
        val orderDetailRef = FirebaseDatabase.getInstance().getReference("OrderDetail")
        val query = orderDetailRef.orderByChild("orderID").equalTo(currentOrderID.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var orderID: Int? = null
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null) { //&& !order.deal
                            // An uncompleted order exists, use its orderID
                            orderID = order.orderID
                            break
                        }
                    }
                    if (orderID != null) {
                        // Step 2: Fetch orderDetails using the obtained orderID
                        val orderDetailsRef =
                            FirebaseDatabase.getInstance().getReference("OrderDetail")
                        val orderDetailQuery =
                            orderDetailsRef.orderByChild("orderID").equalTo(orderID.toString())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(orderDetailsSnapshot: DataSnapshot) {
                                        for (orderDetailData in orderDetailsSnapshot.children) {
                                            val orderDetail =
                                                orderDetailData.getValue(OrderDetail::class.java)

                                            if (orderDetail != null) {
                                                val productID = orderDetail.productID.toString()

                                                // Retrieve product information from Product table
                                                val productRef = FirebaseDatabase.getInstance()
                                                    .getReference("Products").child(productID)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(productSnapshot: DataSnapshot) {
                                                            val product =
                                                                productSnapshot.getValue(Product::class.java)
                                                            if (product != null) {
                                                                // Convert OrderDetail to Cart
                                                                val orderDetailsModel = Cart()
                                                                orderDetailsModel.productID =
                                                                    product.productID
                                                                orderDetailsModel.orderID = orderID
                                                                orderDetailsModel.productName =
                                                                    product.productName
                                                                orderDetailsModel.productPrice =
                                                                    product.productPrice

                                                                orderDetailModels.add(
                                                                    orderDetailsModel
                                                                )

                                                            }
                                                        }

                                                        override fun onCancelled(productError: DatabaseError) {
                                                        }
                                                    })
                                            }
                                        }
                                    }

                                    override fun onCancelled(orderDetailsError: DatabaseError) {
                                    }
                                })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
