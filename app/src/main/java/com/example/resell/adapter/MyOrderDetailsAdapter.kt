package com.example.resell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.database.Order
import com.example.resell.database.OrderDetail

class MyOrderDetailsAdapter(private val orderToDisplay: Order?) :
    RecyclerView.Adapter<MyOrderDetailsAdapter.MyViewHolder>() {

    private val orderDetailsList = ArrayList<OrderDetail>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_history_details,
            parent, false
        )
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = orderDetailsList[position]

        if (currentitem.orderID == orderToDisplay?.orderID) {
            // Found a matching orderId, populate the views with the order details
            holder.orderID.text = currentitem.orderID.toString()
            holder.orderStatus.text = orderToDisplay.orderStatus
            holder.orderPrice.text = orderToDisplay.orderAmount.toString()
            holder.orderDate.text = orderToDisplay.orderDate.toString()
        }


    }

    override fun getItemCount(): Int {
        return orderDetailsList.size
    }

    fun updateOrderList(orderDetailsList: List<Order>) {

        this.orderDetailsList.clear()
        this.orderDetailsList.addAll(orderDetailsList)
        notifyDataSetChanged()

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderID: TextView = itemView.findViewById(R.id.orderID)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderPrice: TextView = itemView.findViewById(R.id.orderPrice)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
    }
}