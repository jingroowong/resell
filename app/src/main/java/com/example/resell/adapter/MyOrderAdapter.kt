package com.example.resell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.OrderHistoryDirections
import com.example.resell.R
import com.example.resell.database.Order
import com.example.resell.listener.IRecyclerClicklistener

class MyOrderAdapter(
    private val context: Context,
    private val orderList: MutableList<Order>,
    private val navController: NavController
) : RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var orderID: TextView? = null
        var orderStatus: TextView? = null
        var orderDate: TextView? = null
        var orderPrice: TextView? = null
        var orderButton: Button? = null

        private var clickListener: IRecyclerClicklistener? = null

        fun setClickListener(clickListener: IRecyclerClicklistener) {
            this.clickListener = clickListener
        }

        init {
            orderID = itemView.findViewById(R.id.orderID) as TextView
            orderStatus = itemView.findViewById(R.id.orderStatus) as TextView
            orderDate = itemView.findViewById(R.id.orderDate) as TextView
            orderPrice = itemView.findViewById(R.id.orderPrice) as TextView
            orderButton = itemView.findViewById(R.id.orderButton) as Button

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, absoluteAdapterPosition)
        }

        //private val orderList = ArrayList<Order>()

//        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//
//            val currentitem = orderList[position]
//
//            holder.orderID.text = currentitem.orderID.toString()
//            holder.orderStatus.text = currentitem.orderStatus
//            holder.orderPrice.text = currentitem.orderAmount.toString()
//            holder.orderDate.text = currentitem.orderDate
//
//            holder.itemView.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    val activity = v!!.context as AppCompatActivity
//                    val orderHistoryDetailsFragment = OrderHistoryDetails()
//
//                    // Get the FragmentManager
//                    val fragmentManager = activity.supportFragmentManager
//
//                    // Begin a transaction
//                    val transaction = fragmentManager.beginTransaction()
//
//                    // Remove the previous fragment (if any) from the FrameLayout
//                    val previousFragment = fragmentManager.findFragmentById(R.id.frameLayout)
//                    if (previousFragment != null) {
//                        transaction.remove(previousFragment)
//                    }
//
//                    // Replace the current fragment in the FrameLayout with the new one
//                    transaction.replace(R.id.frameLayout, orderHistoryDetailsFragment)
//
//                    // Add the transaction to the back stack (optional)
//                    transaction.addToBackStack(null)
//
//                    // Commit the transaction
//                    transaction.commit()
//                }
//            })
//        }

//        fun updateOrderList(orderList: List<Order>) {
//
//            this.orderList.clear()
//            this.orderList.addAll(orderList)
//            notifyDataSetChanged()
//
//        }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val orderID: TextView = itemView.findViewById(R.id.orderID)
            val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
            val orderPrice: TextView = itemView.findViewById(R.id.orderPrice)
            val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.order_history_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.orderID!!.text = StringBuilder().append(orderList[position].orderID)
        holder.orderStatus!!.text = StringBuilder().append(orderList[position].orderStatus)
        holder.orderDate!!.text = StringBuilder().append(orderList[position].orderDate)
        holder.orderPrice!!.text = String.format("RM %.2f", orderList[position].orderAmount)

        holder.setClickListener(object : IRecyclerClicklistener {
            override fun onItemClickListener(view: View?, position: Int) {
                showOrderDetails(orderList[position])
            }
        })
    }

    private fun showOrderDetails(order: Order) {
        val action = OrderHistoryDirections.actionOrderHistoryToOrderHistoryDetails(order)
        navController.navigate(action)
    }

}