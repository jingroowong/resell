package com.example.resell.admin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.OrderHistory
import com.example.resell.OrderHistoryDetails
import com.example.resell.R
import com.example.resell.adapter.MyUpdateOrderAdapter
import com.example.resell.database.Order
import com.example.resell.database.OrderDetails
import com.example.resell.database.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val db = Firebase.database.getReference("Orders")

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateOrderStatus.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateOrderStatus : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        const val ARG_ORDER = "order"
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

        val backBtn = view.findViewById<ImageView>(R.id.btnBack)
        var orderStatus = ""
        val oRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)

        // Get the order data from arguments
        val order = arguments?.getParcelable(OrderHistoryDetails.ARG_ORDER) as Order?
        val product = arguments?.getParcelable(OrderHistoryDetails.ARG_PRODUCT) as Product?

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
        }

        // Fetch and display product data based on order details
        val orderID = order?.orderID ?: 0
        fetchOrderDetailsAndProductData(orderID)

        order?.let {
            if (it.orderStatus == "Processing") {
                oRadioGroup.check(R.id.processingRB)
            } else if (it.orderStatus == "Shipping") {
                oRadioGroup.check(R.id.shippingRB)
            } else if (it.orderStatus == "Delivered") {
                oRadioGroup.check(R.id.deliveredRB)
            }

            oRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                Log.d("FirebaseData", "2Get Order ID Data $orderID")
                when (checkedId) {
                    R.id.processingRB -> {
                        orderStatus = "Processing"
                    }

                    R.id.shippingRB -> {
                        orderStatus = "Shipping"
                    }

                    R.id.deliveredRB -> {
                        orderStatus = "Delivered"
                    }
                }
                Log.d("FirebaseData", "Change Data $orderStatus")
                Log.d("FirebaseData", "3Get Order ID Data $orderID")
            }
            updateBtn.setOnClickListener {
                Log.d("Update", "Button Clicked") // Add this line
                if (!isUpdating) {
                    isUpdating = true
                    updateOrder(orderID!!, orderStatus)
                    Log.d("FirebaseData", "Get Data $orderStatus")
                }
                Log.e("Update", "Update data")

            }

            backBtn.setOnClickListener {
//                findNavController().navigate(R.id.action_updateOrderStatus_to_chooseOrderToUpdate)
                // Use the FragmentManager to pop the current fragment from the back stack
                fragmentManager?.popBackStack()
            }
        }
    }

    private fun updateOrder(orderID: Int, newOrderStatus: String) {
        // Check if the orderID is valid (not equal to -1)
        if (orderID != -1) {
            val ordersRef = db.child(orderID.toString())
            val updateData = mapOf("orderStatus" to newOrderStatus)
            ordersRef.updateChildren(updateData)
                .addOnSuccessListener {
                    Log.d("Update", "Order Status Updated")
                    showSuccessDialog(
                        "Order Status Updated",
                        "The order has been successfully updated."
                    )
                    isUpdating = false
                }
                .addOnFailureListener { error ->
                    Log.e("Update", "Error updating data: ${error.message}")
                    isUpdating = false
                }
        } else {
            // Handle the case where orderID is invalid
            Log.e("Update", "Invalid orderID: $orderID")
            isUpdating = false
        }
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
                recyclerView?.adapter = MyUpdateOrderAdapter(products)
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

    fun showSuccessDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            findNavController().navigate(R.id.action_updateOrderStatus_to_chooseOrderToUpdate)
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_order_status, container, false)
    }


}