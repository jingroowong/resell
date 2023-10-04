package com.example.resell

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.models.Users

class MyAdapter(private val usersList: ArrayList<Users>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        return usersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentUsers = usersList[position]


        holder.tvName.text = usersList[position].username
        holder.tvPhone.text = usersList[position].phone
        holder.tvSign.text = usersList[position].signIn
        holder.tvAdd.text = usersList[position].address



    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val tvName : TextView = itemView.findViewById(R.id.rCusername)
        val tvPhone: TextView = itemView.findViewById(R.id.rCphone)
        val tvSign : TextView = itemView.findViewById(R.id.rCsignIn)
        val tvAdd : TextView = itemView.findViewById(R.id.rcAddress)


    }


}