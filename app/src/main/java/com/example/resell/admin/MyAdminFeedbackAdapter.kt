package com.example.resell.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.models.AdminFeedback

class MyAdminFeedbackAdapter(private val feedbackList:ArrayList<AdminFeedback>) :RecyclerView.Adapter<MyAdminFeedbackAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val adminOrderId : TextView = itemView.findViewById(R.id.rcOrderId)
        val adminFeedback : TextView = itemView.findViewById(R.id.rcFeedback)
        val adminRating : TextView = itemView.findViewById(R.id.rcRating)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.feedback_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.adminOrderId.text = feedbackList[position].orderId.toString()
        holder.adminRating.text = feedbackList[position].rating.toString()
        holder.adminFeedback.text = feedbackList[position].feedback


    }

    override fun getItemCount(): Int {

        return feedbackList.size

    }

}