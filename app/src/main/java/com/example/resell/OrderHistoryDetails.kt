package com.example.resell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.Model.OrderDetailsModel
import com.example.resell.adapter.MyOrderDetailsAdapter
import com.example.resell.database.Order
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
private lateinit var orderDetailRecyclerView: RecyclerView
lateinit var orderDetailsAdapter: MyOrderDetailsAdapter

class OrderHistoryDetails : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history_details, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistoryDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        orderDetailRecyclerView = view.findViewById(R.id.recyclerView)
//        orderDetailRecyclerView.layoutManager = LinearLayoutManager(context)
//        orderDetailRecyclerView.setHasFixedSize(true)
//        orderDetailsAdapter = MyOrderDetailsAdapter()
//        orderDetailRecyclerView.adapter = adapter
//
//        viewModel = ViewModelProvider(this).get(OrderDetailsModel::class.java)
//
//        viewModel.allOrdersDetail.observe(viewLifecycleOwner, Observer {
//
//            orderDetailsAdapter.updateOrderList(it)
//
//        })

        val firestore = FirebaseFirestore.getInstance()
        val ordersCollection = firestore.collection("Order")
        val orderIdToFind = "1"

        ordersCollection.document(orderIdToFind)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val orderData = documentSnapshot.toObject(Order::class.java)

                    // Now 'orderData' contains the specific order data
                    // You can use it as needed
                    if (orderData != null) {
                        // Update the RecyclerView adapter with the retrieved order
                        orderDetailsAdapter.updateOrderList(listOf(orderData))
                    }
                } else {
                    // The document with the specified orderId does not exist
                    // Handle this case accordingly
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while retrieving the document
            }
    }

}
