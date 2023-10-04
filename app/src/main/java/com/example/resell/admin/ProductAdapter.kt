package com.example.resell.admin

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.database.Product
import com.example.resell.database.ProductDao
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(product: Product) {
            val productNameTextView = itemView.findViewById<TextView>(R.id.product_name)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.product_price)
            val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
            val productDateTextView = itemView.findViewById<TextView>(R.id.product_date)
            val productAvailability = itemView.findViewById<TextView>(R.id.product_availability)
            val editBtn = itemView.findViewById<Button>(R.id.editBtn)

            productNameTextView.text = product.productName
            productPriceTextView.text = "RM ${product.productPrice}"

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val timestamp = product.dateUpload
            if (timestamp != null) {
                val date = Date(timestamp)
                val formattedDate = dateFormat.format(date)
                productDateTextView.text = formattedDate
            }

            val storage = Firebase.storage.reference
            val gsReference = storage.child(product.productImage.toString())

            gsReference.downloadUrl.addOnSuccessListener { uri ->
                // Load image into ImageView using Picasso
                Picasso.get()
                    .load(uri)
                    .into(productImageView)
            }.addOnFailureListener { exception ->
               Log.d("FirebaseImage","Load Image from Firebase Failed")
            }



            if (product.productAvailability == false) {
                productAvailability.text = "Sold"

            } else {
                productAvailability.text = "On Sale"
//                productAvailability.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
            }


            editBtn.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("productID", product.productID)
                }

                itemView.findNavController()
                    .navigate(R.id.action_adminViewProduct_to_adminSingleProduct, bundle)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_column, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
