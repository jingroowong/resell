package com.example.resell

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resell.databinding.ActivityAddFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFeedbackBinding
    private lateinit var reference: DatabaseReference

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(userId).child("orderId").get().addOnSuccessListener {
            if (it.exists()) {
                val orderId = it.value.toString()
                binding.idOrder.text = "Order ID : " + orderId
        binding.ratingBar.rating = 2.5f
        binding.ratingBar.stepSize = .5f

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            binding.ratingNum.text = "Rating : $rating"
                    binding.submitBtn.setOnClickListener {
                        reference = FirebaseDatabase.getInstance().getReference("Orders")
                        val user = FirebaseAuth.getInstance().currentUser
                        val userId = user?.uid
                        val feedback = binding.feedbackEditText.text.toString()

                        Toast.makeText(this,"Submit Successfully ! Thank You",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Feedback::class.java)
                        startActivity(intent)
                        finish()


                        if (userId != null && feedback != null) {
                            if (userId == user?.uid) {
                                    reference.child(orderId).child("rating").setValue(rating)
                                reference.child(orderId).child("feedback").setValue(feedback)

                            } else {
                                Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
