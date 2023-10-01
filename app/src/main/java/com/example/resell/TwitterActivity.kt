package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TwitterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       firebaseAuth = FirebaseAuth.getInstance()
        val provider = OAuthProvider.newBuilder("twitter.com")


        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {

            pendingResultTask
                .addOnSuccessListener {
                    val intent = Intent(this, CoverPage::class.java)
                    startActivity(intent)
                   Toast.makeText(this, "Login Successfully",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Try Again ",Toast.LENGTH_SHORT).show()
                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    reference = FirebaseDatabase.getInstance().getReference("Users")
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid
                    val signIn = "TWITTER"
                    if (userId != null) {
                        if (userId == user?.uid) {
                            reference.child(userId).child("signIn").setValue(signIn).toString()

                        }
                    }
                    val intent = Intent(this, CoverPage::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login Successfully",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Try Again ",Toast.LENGTH_SHORT).show()
                }

        }

    }
}