package com.example.resell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.OrderHistoryDetails
import com.example.resell.R
import com.example.resell.database.Order

class MyOrderAdapter : RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {

    private val orderList = ArrayList<Order>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_history_list,
            parent,false
        )
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = orderList[position]

        holder.orderID.text = currentitem.orderID.toString()
        holder.orderStatus.text = currentitem.orderStatus
        holder.orderPrice.text = currentitem.orderAmount.toString()
        holder.orderDate.text = currentitem.orderDate

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val activity = v!!.context as AppCompatActivity
                val orderHistoryDetailsFragment = OrderHistoryDetails()

                // Get the FragmentManager
                val fragmentManager = activity.supportFragmentManager

                // Begin a transaction
                val transaction = fragmentManager.beginTransaction()

                // Remove the previous fragment (if any) from the FrameLayout
                val previousFragment = fragmentManager.findFragmentById(R.id.frameLayout)
                if (previousFragment != null) {
                    transaction.remove(previousFragment)
                }

                // Replace the current fragment in the FrameLayout with the new one
                transaction.replace(R.id.frameLayout, orderHistoryDetailsFragment)

                // Add the transaction to the back stack (optional)
                transaction.addToBackStack(null)

                // Commit the transaction
                transaction.commit()
            }
        })
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateOrderList(orderList : List<Order>){

        this.orderList.clear()
        this.orderList.addAll(orderList)
        notifyDataSetChanged()

    }

    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val orderID : TextView = itemView.findViewById(R.id.orderID)
        val orderStatus : TextView = itemView.findViewById(R.id.orderStatus)
        val orderPrice : TextView = itemView.findViewById(R.id.orderPrice)
        val orderDate : TextView = itemView.findViewById(R.id.orderDate)
    }

}