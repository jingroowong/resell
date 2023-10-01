package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginpage.models.Users
import com.example.resell.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var firebase : Firebase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()



        val logLink = findViewById<TextView>(R.id.loginLink)


        logLink.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.regBtn.setOnClickListener {
            val username = binding.reg.text.toString()
            val pass = binding.pass.text.toString()
            val conPass = binding.conPass.text.toString()
            val email = binding.email.text.toString()
            val address = binding.regAddress.text.toString()

            if (username.isNotEmpty() && pass.isNotEmpty() && conPass.isNotEmpty() && email.isNotEmpty()) {
                if (pass == conPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this,  "Sign Up Successfully", Toast.LENGTH_SHORT).show()

                            reference = FirebaseDatabase.getInstance().getReference("Users")
                            val user = FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid
                            val signIn = "Email and Password"
                            val users = Users(username)
                            if (userId != null) {
                                reference.child(userId).setValue(users)
                                reference.child(userId).child("address").setValue(address)
                                reference.child(userId).child("signIn").setValue(signIn)
                            }
                        }
                    }


                }else{
                    Toast.makeText(this,  "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,  "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }

                }

                binding.Twiiter.setOnClickListener {
                    val intent = Intent(this, TwitterActivity::class.java)
                    startActivity(intent)
                }
                binding.google.setOnClickListener {
                    val intent = Intent(this, GoogleActivity::class.java)
                    startActivity(intent)
                }
                binding.yahoo.setOnClickListener {
                    val provider = OAuthProvider.newBuilder("yahoo.com")
                    provider.addCustomParameter("prompt", "login")
                    provider.addCustomParameter("language", "en")

                    val pendingResultTask = firebaseAuth.pendingAuthResult
                    if (pendingResultTask != null) {

                        pendingResultTask
                            .addOnSuccessListener {
                                val intent = Intent(this, CoverPage::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        firebaseAuth
                            .startActivityForSignInWithProvider(this, provider.build())
                            .addOnSuccessListener {
                                reference = FirebaseDatabase.getInstance().getReference("Users")
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid
                                val username = user?.displayName.toString()
                                val email = user?.email
                                val signIn = "YAHOO"
                                if (userId != null) {
                                    if (userId == user?.uid) {
                                        reference.child(userId).child("signIn").setValue(signIn)
                                    }
                                }
                                val intent = Intent(this, CoverPage::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Try Again ", Toast.LENGTH_SHORT).show()
                            }

                    }

                }


            }
        }





