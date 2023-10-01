package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.models.Orders
import com.example.resell.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Feedback : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var orderArrayList: ArrayList<Orders>
    private lateinit var orders : Orders
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backBT.setOnClickListener {
            val intent = Intent(this, CoverPage::class.java)
            startActivity(intent)
        }




        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.child(userId).child("orderId").get().addOnSuccessListener {
            if(it.exists()){
                val orderId = it.value.toString()
                getData(orderId)
            }

        }




        }





    private fun getData(orderId: String){


        dbref = FirebaseDatabase.getInstance().getReference("Orders")
        dbref.child(orderId.toString()).get().addOnSuccessListener {
            if(it.exists()){
                val orderId = it.child("orderID").value
                val orderAmount = it.child("orderAmount").value
                val orderDate = it.child("orderDate").value
                val orderStatus = it.child("orderStatus").value
                val rating = it.child("rating").value
                val feedback = it.child("feedback").value


                if(rating == null && feedback == null) {
                    binding.orderId.text = orderId.toString()
                    binding.orderAmount.text = orderAmount.toString()
                    binding.orderDate.text = orderDate.toString()
                    binding.orderStatus.text = orderStatus.toString()

                    binding.addFeedback.setOnClickListener {
                        val intent = Intent(this, AddFeedbackActivity::class.java)
                        startActivity(intent)
                    }
                }
                if(rating != null && feedback != null) {
                    binding.zeroPending.text = "There are no feedback to be updated"
                    binding.addFeedback.setOnClickListener {

                        Toast.makeText(this,"No Order to Be updated now",Toast.LENGTH_SHORT).show()
                    }

                }




            }else{
                Toast.makeText(this,"INVALID USER", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }

    }
}


