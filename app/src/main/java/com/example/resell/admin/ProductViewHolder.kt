package com.example.resell.admin

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.database.Product
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(product: Product) {
        val productNameTextView = itemView.findViewById<TextView>(R.id.product_name)
        val productPriceTextView = itemView.findViewById<TextView>(R.id.product_price)
        val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
        val productDateTextView = itemView.findViewById<TextView>(R.id.product_date)

        productNameTextView.text = product.productName
        productPriceTextView.text = "Price: RM${product.productPrice}"

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val timestamp = product.dateUpload
        if (timestamp != null) {
            val date = Date(timestamp)
            val formattedDate = dateFormat.format(date)
            productDateTextView.text = formattedDate
        }


        Picasso.get()
            .load(product.productImage) // Replace with your product's image URL field
            .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder while loading
            .error(R.drawable.ic_launcher_background) // Optional error image to display if loading fails
            .into(productImageView)
    }
}
