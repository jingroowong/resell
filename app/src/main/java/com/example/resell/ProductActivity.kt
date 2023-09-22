package com.example.resell;

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.adapter.MyProductAdapter
import com.example.resell.database.Product
import com.example.resell.listener.IProductLoadListener
import com.example.resell.utills.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

public class ProductActivity : AppCompatActivity(), IProductLoadListener {
    lateinit var productLoadListener: IProductLoadListener
    lateinit var recycler_product: RecyclerView
    lateinit var productLayout : RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_catalog_listing)
        recycler_product= findViewById(R.id.recycler_product)
        productLayout = findViewById<RelativeLayout>(R.id.productLayout)
        init()
        loadProductFromFirebase()
    }

    private fun loadProductFromFirebase() {
        val productModels: MutableList<Product> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Product")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(Product::class.java)
                            productModel!!.key = productSnapshot.key
                            productModels.add(productModel)
                        }
                        productLoadListener.onProductLoadSuccess(productModels)
                    } else
                        productLoadListener.onProductLoadFailed("Product items not exist")

                }

                override fun onCancelled(error: DatabaseError) {
                    productLoadListener.onProductLoadFailed(error.message)
                }

            })
    }

    private fun init() {
        productLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_product.addItemDecoration(SpaceItemDecoration())
    }

    override fun onProductLoadSuccess(productModelList: List<Product>?) {
        val adapter  = MyProductAdapter(this,productModelList!!)

        recycler_product.adapter = adapter
    }

    override fun onProductLoadFailed(message: String?) {

        Snackbar.make(productLayout, message!!, Snackbar.LENGTH_LONG).show()

    }
}