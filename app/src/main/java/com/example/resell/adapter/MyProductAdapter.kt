package com.example.resell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Product

class MyProductAdapter(
    private val context: Context,
    private val list: List<Product>

) : RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {
    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_product_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].productImage)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].productName)
        holder.txtPrice!!.text = StringBuilder("RM").append(list[position].productPrice)
    }
}