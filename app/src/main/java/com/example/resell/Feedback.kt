package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resell.models.Orders
import com.example.resell.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Feedback : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var recyclerview: RecyclerView
    private lateinit var orderArrayList: ArrayList<Orders>
    private lateinit var orders : Orders
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        if (userId != null) {
            Log.d("Order Id ","Order ID $userId" )
        } else {
            // Handle the case where the user is not authenticated
        }

        val databaseReference = FirebaseDatabase.getInstance().reference.child("Orders")

        databaseReference.orderByChild("userID").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val orderArrayList = ArrayList<Orders>() // Initialize the list here

                for (orderSnapshot in dataSnapshot.children) {
                    val orderId = orderSnapshot.key // This is the orderId
                    if (orderId != null) {

                    }
                    if (orderId != null) {
                        val order = orderSnapshot.getValue(Orders::class.java)
                        order?.orderId = orderId.toInt().toLong() // Set the orderId in the Order object
                        if (order != null) {

                        }

                        order?.let {
                            orderArrayList.add(it)

                        }
                    }
                }

                // Set up RecyclerView adapter after retrieving data
                val recyclerview = findViewById<RecyclerView>(R.id.orderRecycler)
                recyclerview.layoutManager = LinearLayoutManager(this@Feedback)
                recyclerview.adapter = MyFeedbackAdapter(this@Feedback,orderArrayList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                println("Error: ${databaseError.message}")
            }
        })

        binding.button2.setOnClickListener {
//            val intent = Intent(this, CoverPage::class.java)
//            startActivity(intent)
            onBackPressed()
        }
    }



}



