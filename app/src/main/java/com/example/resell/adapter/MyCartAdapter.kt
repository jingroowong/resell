package com.example.resell.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Cart
import com.example.resell.eventbus.UpdateCartEvent
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus

class MyCartAdapter(
    private val context: Context,
    private val cartModelList: List<Cart>
) : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {

    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null
        var btnDelete: ImageView? = null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].productImage)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(cartModelList[position].productName)
//        holder.txtPrice!!.text = StringBuilder("RM").append(cartModelList[position].productPrice)
        holder.txtPrice!!.text = String.format("RM %.2f", cartModelList[position].productPrice)

        //Event
        holder.btnDelete!!.setOnClickListener { _ ->
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you want to delete item")
                .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("DELETE") { dialog, _ ->

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(UpdateCartEvent())
                        }
                }
                .create()
            dialog.show()

        }
    }
}