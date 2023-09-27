package com.example.resell.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.R
import com.example.resell.database.Product
import com.example.resell.database.ProductDao
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(product: Product) {
            val productNameTextView = itemView.findViewById<TextView>(R.id.product_name)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.product_price)
            val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
            val productDateTextView = itemView.findViewById<TextView>(R.id.product_date)
            val productAvailability=itemView.findViewById<TextView>(R.id.product_availability)
            val editBtn=itemView.findViewById<Button>(R.id.editBtn)

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


            if(product.productAvailability==false){
                productAvailability.text="Sold"
            }else{
                productAvailability.text="On Sale"
            }


            editBtn.setOnClickListener{
                val bundle = Bundle().apply {
                    putInt("productID", product.productID)
                }

                itemView.findNavController().navigate(R.id.action_adminViewProduct_to_adminSingleProduct,bundle)

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
