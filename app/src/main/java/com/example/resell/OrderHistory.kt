package com.example.resell

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.adapter.MyOrderAdapter
import com.example.resell.database.Order
import com.example.resell.listener.IOrderHistoryListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.resell.databinding.FragmentOrderHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistory.newInstance] factory method to
 * create an instance of this fragment.
 */

private lateinit var orderRecyclerView: RecyclerView
private lateinit var binding: FragmentOrderHistoryBinding
private var orderLoadListener: IOrderHistoryListener? = null

val orderModels: MutableList<Order> = ArrayList()

class OrderHistory : Fragment(), IOrderHistoryListener {
    override fun onResume() {
        super.onResume()
        // Clear the orderDetailModels list when the fragment is resumed
        orderModels.clear()
    }

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
    ): View {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderRecyclerView = binding.recyclerView

        init()
        loadOrderFromFirebase()
    }

    override fun onOrderLoadSuccess(orderModelList: MutableList<Order>) {
        val adapter = MyOrderAdapter(requireContext(), orderModelList, findNavController())
        orderRecyclerView.adapter = adapter
    }

    override fun onOrderLoadFailed(message: String?) {
        TODO("Not yet implemented")
    }


    private fun loadOrderFromFirebase() {
        FirebaseDatabase.getInstance()
            .getReference("Order")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (orderSnapshot in snapshot.children) {
                            val orderModel = orderSnapshot.getValue(Order::class.java)
                            orderModels.add(orderModel!!)
                        }
                        orderLoadListener?.onOrderLoadSuccess(orderModels)
                        Log.d("FirebaseData", "Data retrieved successfully")
                    } else {
                        orderLoadListener?.onOrderLoadFailed("Product items not exist")
                        Log.d("FirebaseData", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    orderLoadListener?.onOrderLoadFailed(error.message)
                }
            })
    }

    private fun init() {
        orderLoadListener = this
        val layoutManager = LinearLayoutManager(requireContext())

        orderRecyclerView.layoutManager = layoutManager
        orderRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )

        binding.frameLayout.setOnClickListener {
            //Navigate to Cart
            val navController =
                this.findNavController().navigate(R.id.action_orderHistory_to_orderHistoryDetails)
        }
    }
}