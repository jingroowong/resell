package com.example.resell;
//
//import android.content.Intent
//import android.media.metrics.Event
//import android.os.Bundle
//import android.util.Log
//import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.resell.adapter.MyProductAdapter
//import com.example.resell.database.Cart
//import com.example.resell.database.Product
//import com.example.resell.eventbus.UpdateCartEvent
//import com.example.resell.listener.ICartLoadListener
//import com.example.resell.listener.IProductLoadListener
//import com.example.resell.utills.SpaceItemDecoration
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.nex3z.notificationbadge.NotificationBadge
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//
//public class ProductActivity : AppCompatActivity(), IProductLoadListener, ICartLoadListener {
//    lateinit var productLoadListener: IProductLoadListener
//    lateinit var cartLoadListener: ICartLoadListener
//    lateinit var badge: NotificationBadge
//    lateinit var recycler_product: RecyclerView
//    lateinit var productLayout: RelativeLayout
//    lateinit var btnCart: FrameLayout
//
//    override fun onStart() {
//        super.onStart()
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
//            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
//        EventBus.getDefault().unregister(this)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public fun onUpdateCartEvent(event: UpdateCartEvent) {
//        countCartFromFirebase()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.product_catalog_listing)
//        recycler_product = findViewById(R.id.recycler_product)
//        productLayout = findViewById<RelativeLayout>(R.id.productLayout)
//        btnCart = findViewById(R.id.btnCart)
//        badge = findViewById(R.id.badge)
//        init()
//        loadProductFromFirebase()
//        countCartFromFirebase()
//    }
//
//    private fun countCartFromFirebase() {
//        val cartModels: MutableList<Cart> = ArrayList()
//        FirebaseDatabase.getInstance()
//            .getReference("Cart")
//            .child("UNIQUE_USER_ID")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    for (cartSnapshot in snapshot.children) {
//                        val cartModel = cartSnapshot.getValue(Cart::class.java)
//                        cartModel!!.key = cartSnapshot.key
//                        cartModels.add(cartModel)
//                    }
//                    cartLoadListener.onLoadCartSuccess(cartModels)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    cartLoadListener.onLoadCartFailed(error.message)
//                }
//            })
//
//    }
//
//    private fun loadProductFromFirebase() {
//        val productModels: MutableList<Product> = ArrayList()
//        FirebaseDatabase.getInstance()
//            .getReference("Product")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        for (productSnapshot in snapshot.children) {
//                            val productModel = productSnapshot.getValue(Product::class.java)
//                            productModel!!.key = productSnapshot.key
//                            productModels.add(productModel)
//                        }
//                        productLoadListener.onProductLoadSuccess(productModels)
//                        Log.d("FirebaseData", "Data retrieved successfully")
//                    } else {
//                        productLoadListener.onProductLoadFailed("Product items not exist")
//                        Log.d("FirebaseData", "No data found")
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    productLoadListener.onProductLoadFailed(error.message)
//                }
//
//            })
//    }
//
//    private fun init() {
//        productLoadListener = this
//        cartLoadListener = this
//
//        val gridLayoutManager = GridLayoutManager(this, 2)
//        recycler_product.addItemDecoration(SpaceItemDecoration())
//        recycler_product.layoutManager = gridLayoutManager
//
//        btnCart.setOnClickListener {
//            startActivity(Intent(this, CartActivity::class.java))
//        }
//
//    }
//
//    override fun onProductLoadSuccess(productModelList: List<Product>?) {
//        val adapter = MyProductAdapter(this, productModelList!!, cartLoadListener)
//        recycler_product.adapter = adapter
//    }
//
//    override fun onProductLoadFailed(message: String?) {
//        Snackbar.make(productLayout, message!!, Snackbar.LENGTH_LONG).show()
//    }
//
//    override fun onLoadCartSuccess(cartList: List<Cart>) {
//        var cartSum = cartList.size
//        badge!!.setNumber(cartSum)
//    }
//
//    override fun onLoadCartFailed(message: String?) {
//        Snackbar.make(productLayout, message!!, Snackbar.LENGTH_LONG).show()
//    }
//}