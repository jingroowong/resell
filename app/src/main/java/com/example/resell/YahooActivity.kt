package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference


class YahooActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var  reference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = OAuthProvider.newBuilder("yahoo.com")
        provider.addCustomParameter("prompt", "login")
        provider.addCustomParameter("language", "fr")

        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {

            pendingResultTask
                .addOnSuccessListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                }
        } else {
            auth
                .startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                }

        }

    }

}








