package com.example.resell.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resell.R
import com.example.resell.database.Cart
import com.example.resell.database.Product
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.example.resell.listener.IRecyclerClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus

class MyProductAdapter(
    private val context: Context,
    private val list: List<Product>,
    private val cartListener: ICartLoadListener
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
        Glide.with(context)
            .load(list[position].productImage)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].productName)
        holder.txtPrice!!.text = StringBuilder("RM").append(list[position].productPrice)
        holder.setClickListener(object : IRecyclerClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
//                addToCart(list[position])
                showProductDetails(list[position])
            }

        })
    }

    private fun showProductDetails(product: Product) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.product_catalog)
        val nameTextView = dialog.findViewById<TextView>(R.id.name_label)
        val priceTextView = dialog.findViewById<TextView>(R.id.price_label)
        val conditionTextView = dialog.findViewById<TextView>(R.id.condition_label2)
        val descTextView = dialog.findViewById<TextView>(R.id.desc_label)
        val addToCartButton = dialog.findViewById<Button>(R.id.add_to_cart_button)

        nameTextView.text = product.productName
        priceTextView.text = StringBuilder("RM").append(product.productPrice)
        conditionTextView.text = product.productCondition
        descTextView.text = product.productDesc
        addToCartButton.setOnClickListener {
            // Add the product to the cart
            addToCart(product)
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun addToCart(product: Product) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(product.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) { //If exist
                        val cartModel = snapshot.getValue(Cart::class.java)
                        val updateData:MutableMap<String,Any> = HashMap()

                        userCart.child(cartModel!!.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                cartListener.onLoadCartFailed("Item already added")
                            }
                            .addOnFailureListener {e-> cartListener.onLoadCartFailed(e.message)}
                        EventBus.getDefault().postSticky(UpdateCartEvent())
                        Log.d("FirebaseData", "Cart Data retrieved successfully")

                    } else { //If item not in cart, add new
                        val cartModel = Cart()
                        cartModel.key = product.key
                        cartModel.productName = product.productName
                        cartModel.productImage = product.productImage
                        cartModel.productPrice = product.productPrice

                        userCart.child(product.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener { e ->
                                cartListener.onLoadCartFailed(e.message)
                            }
                        Log.d("FirebaseData", "No data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })

    }
}