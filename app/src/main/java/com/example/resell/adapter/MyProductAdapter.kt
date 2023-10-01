package com.example.resell.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Product
import com.example.resell.listener.IRecyclerClickListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class MyProductAdapter(
    private val context: Context,
    private val list: List<Product>,
    private val navController: NavController
) : RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {
    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null

        private var clickListener: IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener) {
            this.clickListener = clickListener
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, absoluteAdapterPosition)
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
        val storage = Firebase.storage.reference
        val gsReference = storage.child(list[position].productImage!!.toString())

        gsReference.downloadUrl.addOnSuccessListener { uri ->
            // Load image into ImageView using Picasso
            Picasso.get()
                .load(uri)
                .into(holder.imageView!!)
        }.addOnFailureListener { exception ->
            Log.d("FirebaseImage","Load Image from Firebase Failed")
        }
        holder.txtName!!.text = StringBuilder().append(list[position].productName)
        holder.txtPrice!!.text = String.format("RM %.2f", list[position].productPrice)
        holder.setClickListener(object : IRecyclerClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
                showProductDetails(list[position])
            }
        })
    }

    private fun showProductDetails(product: Product) {
        val action = ProductFragmentDirections.actionProductFragmentToProductDetailFragment(product)
        navController.navigate(action)
    }
}