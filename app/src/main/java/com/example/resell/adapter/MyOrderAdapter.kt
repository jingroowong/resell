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
import com.example.resell.database.Product
import com.example.resell.listener.IRecyclerClickListener

class MyOrderAdapter(
    private val context: Context,
    private val orderList: MutableList<Order>,
    private val productList: MutableList<Product>,
    private val navController: NavController
) : RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var orderID: TextView? = null
        var orderStatus: TextView? = null
        var orderDate: TextView? = null
        var orderPrice: TextView? = null
        var orderButton: Button? = null

        private var clickListener: IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener) {
            this.clickListener = clickListener
        }

        init {
            orderButton = itemView.findViewById(R.id.orderButton) as Button

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, absoluteAdapterPosition)
        }

        fun bind(order: Order, product: Product?, navController: NavController) {
            orderID = itemView.findViewById(R.id.orderID) as TextView
            orderStatus = itemView.findViewById(R.id.orderStatus) as TextView
            orderDate = itemView.findViewById(R.id.orderDate) as TextView
            orderPrice = itemView.findViewById(R.id.orderPrice) as TextView

            orderID!!.text = StringBuilder().append(order.orderID)
            orderStatus!!.text = StringBuilder().append(order.orderStatus)
            orderDate!!.text = StringBuilder().append(order.orderDate)
            orderPrice!!.text = String.format("RM %.2f", order.orderAmount)

            orderButton!!.setOnClickListener {
                if (product != null) {
                    showOrderDetails(order, product, navController)
                }
            }
        }

        private fun showOrderDetails(order: Order, product: Product, navController: NavController) {
            val action = OrderHistoryDirections.actionOrderHistoryToOrderHistoryDetails(order, product)
            navController.navigate(action)
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

        holder.bind(orderList[position], productList[position], navController)
    }
}