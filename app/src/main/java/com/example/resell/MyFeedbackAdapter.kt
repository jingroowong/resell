package com.example.resell

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.models.Orders
import com.google.firebase.database.DatabaseReference

class MyFeedbackAdapter(private val context: Context, private val ordersList:ArrayList<Orders>) : RecyclerView.Adapter<MyFeedbackAdapter.MyViewHolder>(){

    private lateinit var reference: DatabaseReference
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val fbOrderId : TextView = itemView.findViewById(R.id.feedOrder)
        val fbDate : TextView = itemView.findViewById(R.id.feedbackDate)
        val fbAmount : TextView = itemView.findViewById(R.id.feedbackAmount)
        val fbStatus : TextView = itemView.findViewById(R.id.feedbackStatus)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.fbOrderId.text = ordersList[position].orderId.toString()
        holder.fbAmount.text = ordersList[position].orderAmount.toString()
        holder.fbDate.text = ordersList[position].orderDate
        holder.fbStatus.text = ordersList[position].orderStatus.toString().trim()

        val  update: Button = holder.itemView.findViewById(R.id.feedback)

        update.setOnClickListener {

            if (!ordersList[position].orderStatus.equals("Delivered")) {
                Toast.makeText(context, "Only submit feedback while order status is DELIVERED", Toast.LENGTH_SHORT).show()


            }
            else{

                val feedBundle = Bundle().apply {
                    putString("OrderId",ordersList[position].orderId.toString())

                }
                val intent = Intent(holder.itemView.context, AddFeedbackActivity::class.java)
                intent.putExtras(feedBundle) // Pass the bundle data as extras
                holder.itemView.context.startActivity(intent)

            }

        }
    }

    override fun getItemCount(): Int {

        return ordersList.size
    }


}