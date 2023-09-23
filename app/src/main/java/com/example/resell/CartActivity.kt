package com.example.resell

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.adapter.MyCartAdapter
import com.example.resell.database.Cart
import com.example.resell.eventbus.UpdateCartEvent
import com.example.resell.listener.ICartLoadListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartActivity : AppCompatActivity(), ICartLoadListener {
    var cartLoadListener:ICartLoadListener?=null
    lateinit var recycler_cart: RecyclerView
    lateinit var btnBack: ImageView
    lateinit var txtTotal: TextView
    lateinit var productLayout: RelativeLayout

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public fun onUpdateCartEvent(event: UpdateCartEvent) {
       loadCartFromFirebase()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        recycler_cart = findViewById(R.id.recycler_cart)
        btnBack = findViewById(R.id.btnBack)
        txtTotal = findViewById(R.id.txtTotal)
       productLayout = findViewById<RelativeLayout>(R.id.productLayout)

        init()
        loadCartFromFirebase()

    }

    private fun loadCartFromFirebase() {
        val cartModels: MutableList<Cart> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(Cart::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }
            })
    }

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart.layoutManager = layoutManager
        recycler_cart.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener {
            finish()
        }
    }

    override fun onLoadCartSuccess(cartList: List<Cart>) {
        var sum = 0.0
        for (cartModel in cartList){
            sum += cartModel.productPrice!!
        }
        txtTotal.text = StringBuilder("RM").append(sum)
        val adapter  = MyCartAdapter(this, cartList)
        recycler_cart.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(productLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}