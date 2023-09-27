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
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.Model.OrderDetailsModel
import com.example.resell.adapter.MyOrderDetailsAdapter
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail
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
        private const val ARG_ORDER = "order"

        @JvmStatic
        fun newInstance(order: Order) =
            OrderHistory().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ORDER, order)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        orderDetailRecyclerView = view.findViewById(R.id.recyclerViewHD)
//        orderDetailRecyclerView.layoutManager = LinearLayoutManager(context)
//        orderDetailRecyclerView.setHasFixedSize(true)
//        orderDetailsAdapter = MyOrderDetailsAdapter()
//        orderDetailRecyclerView.adapter = orderDetailsAdapter
//
//        viewModel = ViewModelProvider(this).get(OrderDetailsModel::class.java)
//
//        viewModel.allOrdersDetail.observe(viewLifecycleOwner, Observer {
//
//            orderDetailsAdapter.updateOrderList(it)
//
//        })

        // Get the product data from arguments
        val product = arguments?.getParcelable(ARG_ORDER) as Order?

        // Update UI with product details
        product?.let {
            val orderID = view.findViewById<TextView>(R.id.orderID)
            val orderPrice = view.findViewById<TextView>(R.id.orderPrice)
            val orderStatus = view.findViewById<TextView>(R.id.orderStatus)
            val orderDate = view.findViewById<TextView>(R.id.orderDate)

            orderID.text = it.orderID.toString()
            orderPrice.text = getString(R.string.price_format, it.orderAmount)
            orderStatus.text = it.orderStatus
            orderDate.text = it.orderDate.toString()

        }
    }

}
