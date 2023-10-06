package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MyAdapter(private val usersList: ArrayList<Users>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var reference: DatabaseReference




        val tvName: TextView = itemView.findViewById(R.id.rCusername)
        val tvPhone: TextView = itemView.findViewById(R.id.rCphone)
        val tvSign: TextView = itemView.findViewById(R.id.rCsignIn)
        val tvAdd: TextView = itemView.findViewById(R.id.rcAddress)
        val tvUserId : TextView = itemView.findViewById(R.id.rcUserId)

        private val buttonEdit: Button = itemView.findViewById(R.id.edit)


        init {


            buttonEdit.setOnClickListener {
                val bundle = Bundle().apply {

                    putString("UserId",tvUserId.text.toString())
                    putString("username",tvName.text.toString())
                    putString("phone number",tvPhone.text.toString())
                    putString("address",tvAdd.text.toString())


                }

                val intent = Intent(itemView.context, UpdateUserActivity::class.java)
                intent.putExtras(bundle) // Pass the bundle data as extras
                itemView.context.startActivity(intent)


            }





        }




    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvUserId.text = usersList[position].userId
        holder.tvName.text = usersList[position].username
        holder.tvPhone.text = usersList[position].phone
        holder.tvSign.text = usersList[position].signIn
        holder.tvAdd.text = usersList[position].address


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        return usersList.size
    }


}


















